import {
  errorResponse,
  generateGuide,
} from "../../lib/openai-service.mjs";

export async function POST(request) {
  try {
    const body = await request.json();
    return Response.json(await generateGuide(body?.input), {
      headers: { "Cache-Control": "no-store" },
    });
  } catch (error) {
    return errorResponse(error);
  }
}
