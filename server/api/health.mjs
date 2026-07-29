export default function handler(request, response) {
  if (request.method !== "GET") {
    response.setHeader("Allow", "GET");
    return response.status(405).json({ message: "Method not allowed" });
  }

  response.setHeader("Cache-Control", "no-store");
  return response.status(200).json({
    ok: true,
    openAiConfigured: Boolean(process.env.OPENAI_API_KEY),
  });
}
