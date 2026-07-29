import { createServer } from "node:http";

const port = Number.parseInt(process.env.PORT ?? "8787", 10);
const host = process.env.HOST ?? "127.0.0.1";
const apiKey = process.env.OPENAI_API_KEY ?? "";
const model = process.env.OPENAI_MODEL ?? "gpt-5.4-mini";

createServer(async (request, response) => {
  if (request.method === "GET" && request.url === "/health") {
    return sendJson(response, 200, {
      ok: true,
      openAiConfigured: apiKey.length > 0,
    });
  }
  const isVideoAnalysis =
    request.method === "POST" && request.url === "/v1/analyze-video";
  const isGuideGeneration =
    request.method === "POST" && request.url === "/v1/generate-guide";
  if (!isVideoAnalysis && !isGuideGeneration) {
    return sendJson(response, 404, { message: "Not found" });
  }
  if (!apiKey) {
    return sendJson(response, 503, {
      message: "server/.env에 OPENAI_API_KEY를 설정해 주세요.",
    });
  }
  try {
    const body = await readJsonBody(request);
    if (isGuideGeneration) {
      validateGuideRequest(body);
      return sendJson(response, 200, await generateGuide(body.input));
    }
    validateVideoRequest(body);
    return sendJson(response, 200, await analyze(body));
  } catch (error) {
    return sendJson(response, error.statusCode ?? 500, {
      message: error.message ?? "OpenAI 요청을 처리하지 못했습니다.",
    });
  }
}).listen(port, host, () => {
  console.log(`HobbyMate analysis server: http://${host}:${port}`);
});

async function generateGuide(input) {
  return requestOpenAi({
    model,
    store: false,
    input,
    text: {
      format: {
        type: "json_schema",
        name: "weekly_roadmap",
        strict: true,
        schema: {
          type: "object",
          additionalProperties: false,
          properties: {
            title: { type: "string" },
            weeklyRoadmap: {
              type: "array",
              items: {
                type: "object",
                additionalProperties: false,
                properties: {
                  week: { type: "integer" },
                  theme: { type: "string" },
                  sessionsPerWeek: { type: "integer" },
                  minutesPerSession: { type: "integer" },
                },
                required: [
                  "week",
                  "theme",
                  "sessionsPerWeek",
                  "minutesPerSession",
                ],
              },
            },
          },
          required: ["title", "weeklyRoadmap"],
        },
      },
    },
  });
}

async function analyze(video) {
  const prompt = [
    `취미: ${video.hobbyName}`,
    `사용자 목표: ${video.goal}`,
    `영상 제목: ${video.title}`,
    `채널: ${video.channelName}`,
    `영상 설명: ${video.description || "설명 없음"}`,
    "",
    "제공된 영상 메타데이터와 썸네일을 바탕으로 사용자가 영상을 보며 따라 할 수 있는 순차적인 한국어 실습 체크리스트를 만드세요.",
    "체크리스트는 3~5단계로 구성하고 각 단계에 구체적인 실습 행동과 예상 소요 시간을 포함하세요.",
    "안전 주의가 필요한 활동이라면 해당 단계 설명에 주의사항을 포함하세요.",
    "사용자의 취미 목표와 영상 주제에 직접 관련된 단계만 만드세요.",
    "영상에서 확인할 수 없는 구체적인 사실은 지어내지 마세요.",
  ].join("\n");
  const content = [{ type: "input_text", text: prompt }];
  if (/^https:\/\/i\.ytimg\.com\//.test(video.thumbnailUrl)) {
    content.push({
      type: "input_image",
      image_url: video.thumbnailUrl,
      detail: "low",
    });
  }
  const body = await requestOpenAi({
    model,
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
                  estimatedMinutes: { type: "integer", minimum: 1, maximum: 60 },
                },
                required: ["title", "description", "estimatedMinutes"],
              },
            },
          },
          required: ["title", "estimatedMinutes", "steps"],
        },
      },
    },
  });
  const output = body.output
    ?.flatMap((item) => item.content ?? [])
    .find((item) => item.type === "output_text")?.text;
  if (!output) throw new Error("OpenAI 응답에 체크리스트가 없습니다.");
  return JSON.parse(output);
}

async function requestOpenAi(payload) {
  const result = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${apiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });
  const body = await result.json();
  if (!result.ok) {
    const error = new Error(body.error?.message ?? "OpenAI API 요청에 실패했습니다.");
    error.statusCode = result.status;
    throw error;
  }
  return body;
}

function validateVideoRequest(body) {
  const required = [
    "videoId",
    "title",
    "thumbnailUrl",
    "channelName",
    "hobbyName",
    "goal",
  ];
  if (!body || required.some((key) => !body[key]?.trim?.())) {
    const error = new Error("영상 분석 요청값을 확인해 주세요.");
    error.statusCode = 400;
    throw error;
  }
}

function validateGuideRequest(body) {
  if (!body || typeof body.input !== "string" || !body.input.trim()) {
    const error = new Error("로드맵 생성 요청값을 확인해 주세요.");
    error.statusCode = 400;
    throw error;
  }
}

async function readJsonBody(request) {
  const chunks = [];
  let size = 0;
  for await (const chunk of request) {
    size += chunk.length;
    if (size > 128 * 1024) {
      const error = new Error("요청 본문이 너무 큽니다.");
      error.statusCode = 413;
      throw error;
    }
    chunks.push(chunk);
  }
  return JSON.parse(Buffer.concat(chunks).toString("utf8"));
}

function sendJson(response, status, body) {
  response.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store",
  });
  response.end(JSON.stringify(body));
}
