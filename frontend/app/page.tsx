'use client';

import dynamic from 'next/dynamic';

const Dashboard = dynamic(() => import('./components/Dashboard'), {
  ssr: false,
  loading: () => (
    <div className="flex min-h-screen items-center justify-center bg-[#f8fafc] text-slate-600">
      <div className="flex flex-col items-center gap-5">
        <img src="/logo.svg" alt="Aegis" className="w-16 h-16 animate-pulse" />
        <div className="flex flex-col items-center gap-2">
          <div className="h-1 w-32 bg-indigo-100 rounded-full overflow-hidden">
            <div className="h-full bg-gradient-to-r from-indigo-500 to-violet-500 rounded-full animate-[pulse_1.5s_ease-in-out_infinite]" />
          </div>
          <p className="font-mono text-xs tracking-[0.25em] text-indigo-600 font-semibold uppercase">
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
