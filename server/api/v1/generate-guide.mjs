import { generateGuide } from "../../lib/openai-service.mjs";

export default async function handler(request, response) {
  if (request.method !== "POST") {
    response.setHeader("Allow", "POST");
    return response.status(405).json({ message: "Method not allowed" });
  }

  response.setHeader("Cache-Control", "no-store");
  try {
    const body = parseBody(request.body);
    return response.status(200).json(await generateGuide(body?.input));
  } catch (error) {
    return response.status(error?.statusCode ?? 500).json({
      message: error?.message ?? "요청을 처리하지 못했습니다.",
    });
  }
}

function parseBody(body) {
  if (typeof body === "string") return JSON.parse(body);
  return body ?? {};
}
