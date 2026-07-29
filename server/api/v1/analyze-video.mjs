import {
  analyzeVideo,
  errorResponse,
} from "../../lib/openai-service.mjs";

export async function POST(request) {
  try {
    return Response.json(await analyzeVideo(await request.json()), {
      headers: { "Cache-Control": "no-store" },
    });
  } catch (error) {
    return errorResponse(error);
  }
}
