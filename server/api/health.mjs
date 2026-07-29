export function GET() {
  return Response.json(
    {
      ok: true,
      openAiConfigured: Boolean(process.env.OPENAI_API_KEY),
    },
    { headers: { "Cache-Control": "no-store" } },
  );
}
