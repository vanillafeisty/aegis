'use client';

import React, { useState } from 'react';

interface AegisLogoProps {
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'hero';
  showText?: boolean;
  variant?: 'emblem' | 'full' | 'badge';
  withGlow?: boolean;
  className?: string;
  useImage?: boolean;
}

export default function AegisLogo({
  size = 'md',
  showText = false,
  variant = 'full',
  withGlow = true,
  className = '',
  useImage = true,
}: AegisLogoProps) {
  const [imageError, setImageError] = useState(false);

  // Size mapping in pixels
  const sizeMap = {
    xs: { px: 24, text: 'text-xs', gap: 'gap-1.5' },
    sm: { px: 32, text: 'text-sm', gap: 'gap-2' },
    md: { px: 42, text: 'text-lg', gap: 'gap-2.5' },
    lg: { px: 56, text: 'text-2xl', gap: 'gap-3' },
    xl: { px: 80, text: 'text-3xl', gap: 'gap-4' },
    hero: { px: 110, text: 'text-4xl', gap: 'gap-4' },
  };

  const { px, text, gap } = sizeMap[size] || sizeMap.md;

  return (
    <div className={`inline-flex items-center ${gap} select-none ${className}`}>
      {/* Logo Emblem Container */}
      <div
        className={`relative flex items-center justify-center shrink-0 transition-transform duration-300 hover:scale-105 ${
          variant === 'badge' ? 'p-1.5 rounded-2xl bg-[#0b1728]/80 border border-cyan-500/30' : ''
        }`}
        style={{ width: px, height: px }}
      >
        {/* Ambient Halo Glow */}
        {withGlow && (
          <div
            className="absolute inset-0 rounded-full bg-cyan-400/20 blur-md pointer-events-none animate-pulse-slow"
            style={{ transform: 'scale(1.2)' }}
          />
        )}

        {/* Logo Image or High-Precision Vector SVG */}
        {useImage && !imageError ? (
          <img
            src="/aegis-logo.png"
            alt="AEGIS AI Logo"
            className="w-full h-full object-contain rounded-xl relative z-10 drop-shadow-[0_0_12px_rgba(0,229,255,0.4)]"
            onError={() => setImageError(true)}
          />
        ) : (
          <svg
            viewBox="0 0 200 200"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className="w-full h-full relative z-10 drop-shadow-[0_4px_16px_rgba(0,217,255,0.35)]"
          >
            <defs>
              {/* Deep Navy Blue Gradient for A Left Leg */}
              <linearGradient id="aegis_navy_left" x1="40" y1="160" x2="100" y2="40" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stopColor="#0c2340" />
                <stop offset="50%" stopColor="#123b6b" />
                <stop offset="100%" stopColor="#1e5899" />
              </linearGradient>

              {/* Deep Navy Blue Gradient for A Right Leg */}
              <linearGradient id="aegis_navy_right" x1="160" y1="160" x2="100" y2="40" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stopColor="#0a1d35" />
                <stop offset="60%" stopColor="#103663" />
                <stop offset="100%" stopColor="#1d5494" />
              </linearGradient>

              {/* Specular Highlight for Left Leg */}
              <linearGradient id="aegis_highlight" x1="60" y1="70" x2="80" y2="130" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stopColor="#38bdf8" stopOpacity="0.8" />
                <stop offset="100%" stopColor="#0c2340" stopOpacity="0" />
              </linearGradient>

              {/* Luminous Electric Cyan Wave Ribbon Gradient */}
              <linearGradient id="aegis_cyan_wave" x1="45" y1="125" x2="160" y2="100" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stopColor="#0284c7" />
                <stop offset="35%" stopColor="#00e5ff" />
                <stop offset="70%" stopColor="#38bdf8" />
                <stop offset="100%" stopColor="#06b6d4" />
              </linearGradient>

              {/* Cyber Node Radial Gradients */}
              <radialGradient id="node_cyan_glow" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stopColor="#ffffff" />
                <stop offset="40%" stopColor="#00f0ff" />
                <stop offset="100%" stopColor="#0284c7" />
              </radialGradient>

              <radialGradient id="node_teal_glow" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stopColor="#e0f2fe" />
                <stop offset="45%" stopColor="#38bdf8" />
                <stop offset="100%" stopColor="#0c4a6e" />
              </radialGradient>

              <radialGradient id="node_deep_glow" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stopColor="#38bdf8" />
                <stop offset="60%" stopColor="#0f3b73" />
                <stop offset="100%" stopColor="#071b36" />
              </radialGradient>

              {/* Glow Filter */}
              <filter id="cyan_glow_filter" x="-20%" y="-20%" width="140%" height="140%">
                <feGaussianBlur stdDeviation="3" result="blur" />
                <feComposite in="SourceGraphic" in2="blur" operator="over" />
              </filter>
            </defs>

            {/* Background Ambient Glow */}
            <circle cx="100" cy="100" r="70" fill="#00e5ff" fillOpacity="0.08" filter="url(#cyan_glow_filter)" />

            {/* ================= LEFT NEURAL NETWORK WING ================= */}
            <g id="left-network" opacity="0.95">
              {/* Lattice Connection Lines */}
              <line x1="42" y1="85" x2="62" y2="60" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.65" />
              <line x1="62" y1="60" x2="88" y2="58" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.65" />
              <line x1="42" y1="85" x2="52" y2="105" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.5" />
              <line x1="52" y1="105" x2="75" y2="92" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.6" />
              <line x1="62" y1="60" x2="75" y2="92" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.75" />
              <line x1="75" y1="92" x2="96" y2="68" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.7" />
              <line x1="42" y1="85" x2="32" y2="98" stroke="#00d9ff" strokeWidth="1.5" strokeOpacity="0.4" />
              <line x1="32" y1="98" x2="52" y2="105" stroke="#00d9ff" strokeWidth="1.5" strokeOpacity="0.4" />

              {/* Neural Nodes */}
              <circle cx="42" cy="85" r="7.5" fill="url(#node_deep_glow)" stroke="#00e5ff" strokeWidth="1.2" />
              <circle cx="32" cy="98" r="6" fill="url(#node_deep_glow)" stroke="#00e5ff" strokeWidth="1" />
              <circle cx="62" cy="60" r="8" fill="url(#node_cyan_glow)" filter="url(#cyan_glow_filter)" />
              <circle cx="52" cy="105" r="6.5" fill="url(#node_deep_glow)" stroke="#00e5ff" strokeWidth="1" />
              <circle cx="75" cy="92" r="7.5" fill="url(#node_cyan_glow)" filter="url(#cyan_glow_filter)" />
              <circle cx="88" cy="58" r="6.5" fill="url(#node_teal_glow)" />
            </g>

            {/* ================= RIGHT NEURAL NETWORK WING ================= */}
            <g id="right-network" opacity="0.95">
              {/* Lattice Connection Lines */}
              <line x1="112" y1="58" x2="138" y2="60" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.65" />
              <line x1="138" y1="60" x2="158" y2="85" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.65" />
              <line x1="138" y1="60" x2="125" y2="92" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.75" />
              <line x1="125" y1="92" x2="104" y2="68" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.7" />
              <line x1="125" y1="92" x2="148" y2="105" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.6" />
              <line x1="158" y1="85" x2="148" y2="105" stroke="#00d9ff" strokeWidth="1.8" strokeOpacity="0.5" />
              <line x1="158" y1="85" x2="168" y2="98" stroke="#00d9ff" strokeWidth="1.5" strokeOpacity="0.4" />
              <line x1="168" y1="98" x2="148" y2="105" stroke="#00d9ff" strokeWidth="1.5" strokeOpacity="0.4" />

              {/* Neural Nodes */}
              <circle cx="158" cy="85" r="7.5" fill="url(#node_cyan_glow)" filter="url(#cyan_glow_filter)" />
              <circle cx="168" cy="98" r="6" fill="url(#node_deep_glow)" stroke="#00e5ff" strokeWidth="1" />
              <circle cx="138" cy="60" r="8" fill="url(#node_cyan_glow)" filter="url(#cyan_glow_filter)" />
              <circle cx="148" cy="105" r="6.5" fill="url(#node_cyan_glow)" />
              <circle cx="125" cy="92" r="7" fill="url(#node_teal_glow)" />
              <circle cx="112" cy="58" r="6.5" fill="url(#node_cyan_glow)" />
            </g>

            {/* ================= STYLIZED 'A' BACKBONE ================= */}
            {/* Left Leg: Curved Navy Ribbon */}
            <path
              d="M100 48L62 136C58 145 52 153 43 158C36 162 42 172 50 170C64 167 76 153 82 138L100 95L118 138C124 153 136 167 150 170C158 172 164 162 157 158C148 153 142 145 138 136L100 48Z"
              fill="url(#aegis_navy_right)"
            />

            {/* Left Leg Main Ribbon (Curved Tubular Form) */}
            <path
              d="M100 48L60 140C55 152 46 162 35 165C30 166 32 173 38 173C52 173 66 159 74 142L98 86"
              stroke="url(#aegis_navy_left)"
              strokeWidth="20"
              strokeLinecap="round"
              strokeLinejoin="round"
            />

            {/* Right Leg Main Ribbon */}
            <path
              d="M100 48L140 140C145 152 154 162 165 165C170 166 168 173 162 173C148 173 134 159 126 142L102 86"
              stroke="url(#aegis_navy_right)"
              strokeWidth="20"
              strokeLinecap="round"
              strokeLinejoin="round"
            />

            {/* Specular Highlight on Left Leg */}
            <path
              d="M96 56L68 126"
              stroke="url(#aegis_highlight)"
              strokeWidth="4"
              strokeLinecap="round"
            />

            {/* ================= LUMINOUS CYAN WAVE CROSSBAR ================= */}
            {/* The signature organic cyan wave flowing across the crossbar of the A */}
            <path
              d="M48 132C62 126 78 114 96 112C116 110 134 122 152 108C156 105 158 100 156 95C154 90 146 88 140 92C124 103 108 94 92 98C74 102 60 114 46 122C42 124 42 130 48 132Z"
              fill="url(#aegis_cyan_wave)"
              filter="url(#cyan_glow_filter)"
            />

            {/* Secondary Fluid Streamline */}
            <path
              d="M54 127C72 118 88 108 106 108C124 108 138 116 150 102"
              stroke="#ffffff"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeOpacity="0.8"
            />

            {/* ================= APEX GLOWING NODE ================= */}
            <circle cx="100" cy="48" r="9" fill="url(#node_cyan_glow)" filter="url(#cyan_glow_filter)" />
            <circle cx="100" cy="48" r="4.5" fill="#ffffff" />
          </svg>
        )}
      </div>

      {/* Brand Typography (AEGIS) */}
      {showText && (
        <div className="flex flex-col justify-center">
          <div className="flex items-center gap-2">
            <span
              className={`font-extrabold tracking-[0.2em] uppercase font-sans ${text} bg-gradient-to-r from-white via-cyan-100 to-cyan-400 bg-clip-text text-transparent`}
              style={{ letterSpacing: '0.18em' }}
            >
              AEGIS
            </span>
            <span className="px-1.5 py-0.5 text-[9px] font-mono font-bold tracking-wider rounded-md bg-cyan-500/15 text-cyan-300 border border-cyan-500/35">
              AI
            </span>
          </div>
          {size !== 'xs' && size !== 'sm' && (
            <span className="text-[10px] font-mono tracking-wider text-cyan-400/80 -mt-0.5">
              AUTONOMOUS OUTREACH
            </span>
          )}
        </div>
      )}
    </div>
  );
}
