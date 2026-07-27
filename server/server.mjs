import { createServer } from "node:http";

const port = Number.parseInt(process.env.PORT ?? "8787", 10);
const openAiApiKey = process.env.OPENAI_API_KEY ?? "";
const openAiModel = process.env.OPENAI_MODEL ?? "gpt-5.4-mini";

const server = createServer(async (request, response) => {
  if (request.method === "GET" && request.url === "/health") {
    return sendJson(response, 200, {
      ok: true,
      openAiConfigured: openAiApiKey.length > 0,
    });
  }

  if (request.method !== "POST" || request.url !== "/v1/analyze-video") {
    return sendJson(response, 404, { message: "Not found" });
  }

  if (openAiApiKey.length === 0) {
    return sendJson(response, 503, {
      message: "server/.env에 OPENAI_API_KEY를 설정해 주세요.",
    });
  }

  try {
    const body = await readJsonBody(request);
    validateAnalysisRequest(body);
    const checklist = await analyzeVideoMetadata(body);
    return sendJson(response, 200, checklist);
  } catch (error) {
    const status = error.statusCode ?? 500;
    return sendJson(response, status, {
      message: error.message ?? "영상 정보를 분석하지 못했어요.",
    });
  }
});

server.listen(port, "0.0.0.0", () => {
  console.log(`HobbyMate OpenAI proxy listening on http://0.0.0.0:${port}`);
});

async function analyzeVideoMetadata(video) {
  const prompt = [
    `취미: ${video.hobbyName}`,
    `사용자 목표: ${video.goal}`,
    `영상 제목: ${video.title}`,
    `채널: ${video.channelName}`,
    `영상 설명: ${video.description || "설명 없음"}`,
    "",
    "제공된 영상 메타데이터와 썸네일을 바탕으로 사용자가 영상을 보며 따라 할 수 있는",
    "순차적인 실습 체크리스트를 한국어로 작성하세요.",
    "3~5단계로 구성하고, 안전상 주의가 필요한 활동은 해당 단계 설명에 주의사항을 포함하세요.",
    "영상에서 확인할 수 없는 구체적인 사실을 지어내지 마세요.",
  ].join("\n");

  const content = [{ type: "input_text", text: prompt }];
  if (/^https:\/\/i\.ytimg\.com\//.test(video.thumbnailUrl)) {
    content.push({
      type: "input_image",
      image_url: video.thumbnailUrl,
      detail: "low",
    });
  }

  const openAiResponse = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${openAiApiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: openAiModel,
      store: false,
      input: [{ role: "user", content }],
      text: {
        format: {
          type: "json_schema",
          name: "hobby_video_checklist",
          strict: true,
          schema: {
            type: "object",
            additionalProperties: false,
            properties: {
              title: { type: "string" },
              estimatedMinutes: { type: "integer", minimum: 1, maximum: 180 },
              steps: {
                type: "array",
                minItems: 3,
                maxItems: 5,
                items: {
                  type: "object",
                  additionalProperties: false,
                  properties: {
                    title: { type: "string" },
                    description: { type: "string" },
                    estimatedMinutes: {
                      type: "integer",
                      minimum: 1,
                      maximum: 60,
                    },
                  },
                  required: ["title", "description", "estimatedMinutes"],
                },
              },
            },
            required: ["title", "estimatedMinutes", "steps"],
          },
        },
      },
    }),
  });

  const rawResponse = await openAiResponse.json();
  if (!openAiResponse.ok) {
    const error = new Error(
      rawResponse.error?.message ?? "OpenAI API 요청에 실패했습니다.",
    );
    error.statusCode = openAiResponse.status;
    throw error;
  }

  const outputText = rawResponse.output
    ?.flatMap((item) => item.content ?? [])
    .find((item) => item.type === "output_text")?.text;
  if (!outputText) throw new Error("OpenAI 응답에 체크리스트가 없습니다.");
  return JSON.parse(outputText);
}

function validateAnalysisRequest(body) {
  const requiredFields = [
    "videoId",
    "title",
    "thumbnailUrl",
    "channelName",
    "hobbyName",
    "goal",
  ];
  if (
    !body ||
    requiredFields.some(
      (field) => typeof body[field] !== "string" || body[field].trim() === "",
    )
  ) {
    const error = new Error("영상 분석 요청값을 확인해 주세요.");
    error.statusCode = 400;
    throw error;
  }
}

async function readJsonBody(request) {
  const chunks = [];
  let totalBytes = 0;
  for await (const chunk of request) {
    totalBytes += chunk.length;
    if (totalBytes > 128 * 1024) {
      const error = new Error("요청 본문이 너무 큽니다.");
      error.statusCode = 413;
      throw error;
    }
    chunks.push(chunk);
  }
  return JSON.parse(Buffer.concat(chunks).toString("utf8"));
}

function sendJson(response, statusCode, body) {
  response.writeHead(statusCode, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store",
  });
  response.end(JSON.stringify(body));
}
