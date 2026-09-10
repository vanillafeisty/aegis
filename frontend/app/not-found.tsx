export default function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen text-center p-4 bg-[#f8fafc] text-slate-900">
      <h2 className="text-2xl font-bold mb-2">404 - Page Not Found</h2>
      <a href="/" className="text-indigo-600 hover:underline">Return Home</a>
    </div>
  );
}
