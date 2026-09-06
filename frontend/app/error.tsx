'use client';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="bg-[#07090e] text-white flex flex-col items-center justify-center min-h-screen">
      <h2 className="text-xl font-bold mb-4">Something went wrong</h2>
      <button
        onClick={() => reset()}
        className="px-4 py-2 bg-cyan-600 rounded text-sm hover:bg-cyan-500 transition"
      >
        Try again
      </button>
    </div>
  );
}
