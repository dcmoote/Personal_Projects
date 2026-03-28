export default {
  async fetch(request, env) {
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'POST, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type',
        },
      });
    }

    if (request.method !== 'POST') {
      return new Response('Method not allowed', { status: 405 });
    }

    let body;
    try {
      body = await request.json();
    } catch {
      return new Response('Invalid JSON', { status: 400 });
    }

    const {
      type,
      genres = [],
      mediums = [],
      subjects = [],
      themes = [],
      directionLevel = 2,
    } = body;

    const systemPrompt = buildSystemPrompt(type, genres, mediums, subjects, themes, Number(directionLevel));

    const response = await fetch('https://api.anthropic.com/v1/messages', {
      method: 'POST',
      headers: {
        'x-api-key': env.ANTHROPIC_API_KEY,
        'anthropic-version': '2023-06-01',
        'content-type': 'application/json',
      },
      body: JSON.stringify({
        model: 'claude-haiku-4-5-20251001',
        max_tokens: 400,
        temperature: 1.0,
        system: systemPrompt,
        messages: [{ role: 'user', content: 'Generate one prompt now.' }],
      }),
    });

    if (!response.ok) {
      const err = await response.text();
      return new Response(`Anthropic error: ${err}`, { status: 502 });
    }

    const data = await response.json();
    const prompt = data.content?.[0]?.text?.trim();

    if (!prompt) {
      return new Response('No prompt returned', { status: 502 });
    }

    return new Response(JSON.stringify({ prompt }), {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  },
};

// Random seeds injected into the prompt to force variety
const TIME_PERIODS = [
  'ancient', 'medieval', '1700s', '1920s', '1940s', '1970s', 'present day', 'near future', 'far future', 'post-apocalyptic'
];
const MOODS = [
  'melancholic', 'tense', 'hopeful', 'eerie', 'triumphant', 'lonely', 'frantic', 'serene', 'unsettling', 'bittersweet', 'ominous', 'tender'
];
const SETTINGS = [
  'urban', 'rural', 'wilderness', 'underground', 'coastal', 'rooftop', 'interior', 'desert', 'forest', 'industrial', 'underwater', 'aerial'
];

function pick(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function pickFrom(arr) {
  if (!arr || arr.length === 0) return null;
  return arr[Math.floor(Math.random() * arr.length)];
}

function buildSystemPrompt(type, genres, mediums, subjects, themes, directionLevel) {
  const isBoth = type === 'BOTH';
  const resolvedType = isBoth ? (Math.random() > 0.5 ? 'WRITING' : 'ART') : type;

  // Random seeds to prevent repetition
  const timePeriod = pick(TIME_PERIODS);
  const mood = pick(MOODS);
  const setting = pick(SETTINGS);

  // Theme block
  let themeBlock = '';
  if (themes.length === 1) {
    themeBlock = `THEME: ${themes[0]}
The entire prompt must be built around this theme. The theme is the core identity of the prompt — every element (subject, setting, mood, conflict) must reflect it. Do NOT include the word "${themes[0]}" or any theme label in your output — the theme must be expressed through the content itself.`;
  } else if (themes.length > 1) {
    themeBlock = `THEMES: ${themes.join(' + ')}
The prompt must fuse all of these themes into one coherent concept — find the intersection, not a list. Do NOT name the themes in your output — express them through the content itself.`;
  }

  if (resolvedType === 'WRITING') {
    const genre = pickFrom(genres) || 'fiction';
    const genreList = genres.join(', ') || 'fiction';

    const outputRule = directionLevel === 1
      ? `OUTPUT — MINIMAL (2–6 words only):
Return a raw concept seed. No sentences, no verbs telling them what to do, no explanation. Just the core idea as a phrase.
Correct: "The last confession" / "Borrowed name, borrowed life"
Wrong: "Write about..." / "A character who..." / Any full sentence.`
      : directionLevel === 3
      ? `OUTPUT — DETAILED (3–5 sentences):
Write a fully-formed starting scene with a specific named or described character, exact setting (location + time), the inciting situation or conflict, and the emotional stakes. Make every decision for the writer — they should be able to open a document and start writing immediately without deciding anything themselves. Be concrete and specific throughout.`
      : `OUTPUT — GUIDED (1–2 sentences):
Describe a specific scenario with a clear character situation and an unresolved tension. No resolution. No instructions ("write about", "imagine", "explore"). Start mid-situation.`;

    return `You generate a single creative writing prompt. Genre: ${genre} (user also likes: ${genreList}).
Variety seeds for this prompt — incorporate naturally: time period: ${timePeriod}, mood: ${mood}, setting type: ${setting}.
${themeBlock}

${outputRule}

STRICT RULES:
- Output ONLY the prompt text. Nothing else — no title, no label, no "Here is your prompt", no theme name, no postscript.
- Do not start with "Write", "Imagine", "Explore", or "Create".
- The genre must be recognizable from the content.
- Use the variety seeds to make this prompt feel different from obvious or generic choices.`;
  }

  // ART
  const medium = pickFrom(mediums) || 'your preferred medium';
  const subject = pickFrom(subjects) || 'a figure';
  const mediumList = mediums.join(', ') || 'any medium';
  const subjectList = subjects.join(', ') || 'any subject';

  const outputRule = directionLevel === 1
    ? `OUTPUT — MINIMAL (2–6 words only):
Return a pure visual concept as a short phrase. No sentences, no instructions, nothing telling them what to draw.
Correct: "Rusted gate, lantern glow" / "Hollow-eyed porcelain mask"
Wrong: "Draw a..." / "Paint a scene of..." / Any full sentence.`
    : directionLevel === 3
    ? `OUTPUT — DETAILED (3–5 sentences):
Give the artist a complete visual brief: exact subject, specific setting, composition direction (what's in foreground/background, framing), quality and direction of light, the mood the piece should evoke, and two or three specific visual details to include. The artist should be able to pick up a brush immediately without making a single creative decision themselves.`
    : `OUTPUT — GUIDED (1–2 sentences):
Describe the subject and the visual mood of the scene. Include one specific detail that grounds the image. No composition or lighting instructions — just what the scene is and feels like.`;

  return `You generate a single visual art prompt. Medium: ${medium} (user also works in: ${mediumList}). Subject focus: ${subject} (user also draws: ${subjectList}).
Variety seeds for this prompt — incorporate naturally: time period: ${timePeriod}, mood: ${mood}, setting type: ${setting}.
${themeBlock}

${outputRule}

STRICT RULES:
- Output ONLY the prompt text. Nothing else — no title, no label, no "Here is your prompt", no theme name, no postscript.
- Do not start with "Draw", "Paint", "Sketch", "Create", or "Depict".
- The medium (${medium}) should be mentioned naturally only in Detailed level, not Minimal or Guided.
- Use the variety seeds to make this prompt feel different from obvious or generic choices.
- Never mention the theme by name in the output.`;
}
