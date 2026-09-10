'use client';

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html lang="en">
      <body className="bg-[#f8fafc] text-slate-900 flex flex-col items-center justify-center min-h-screen">
        <h2 className="text-xl font-bold mb-4">Something went wrong</h2>
        <button
          onClick={() => reset()}
          className="px-4 py-2 bg-indigo-600 rounded text-sm text-white hover:bg-indigo-700 transition"
        >
          Try again
        </button>
      </body>
    </html>
  );
}
