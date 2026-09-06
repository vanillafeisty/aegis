'use client';

import dynamic from 'next/dynamic';

const Dashboard = dynamic(() => import('./components/Dashboard'), {
  ssr: false,
  loading: () => (
    <div className="flex min-h-screen items-center justify-center bg-[#060b14] text-slate-200">
      <div className="flex flex-col items-center gap-5">
        <div className="relative">
          <div className="absolute inset-0 rounded-full bg-cyan-400/25 blur-xl animate-pulse" />
          <img
            src="/aegis-logo.png"
            alt="AEGIS AI"
            className="w-16 h-16 object-contain relative z-10 drop-shadow-[0_0_20px_rgba(0,229,255,0.5)] animate-pulse"
          />
        </div>
        <div className="flex flex-col items-center gap-2">
          <div className="h-1 w-32 bg-cyan-950 rounded-full overflow-hidden">
            <div className="h-full bg-gradient-to-r from-cyan-400 to-blue-500 rounded-full animate-[pulse_1.5s_ease-in-out_infinite]" />
          </div>
          <p className="font-mono text-xs tracking-[0.25em] text-cyan-300 font-semibold uppercase">
            Initializing AEGIS AI v2.0...
          </p>
        </div>
      </div>
    </div>
  ),
});

export default function Page() {
  return <Dashboard />;
}
