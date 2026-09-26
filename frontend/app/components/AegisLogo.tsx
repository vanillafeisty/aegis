import React from 'react';

interface AegisLogoProps {
  size?: 'xs' | 'sm' | 'md' | 'lg';
  className?: string;
}

const sizeMap = { xs: 20, sm: 28, md: 36, lg: 56 };

export default function AegisLogo({ size = 'md', className = '' }: AegisLogoProps) {
  const px = sizeMap[size];
  return (
    <img
      src="/logo.svg"
      alt="Aegis"
      width={px}
      height={px}
      className={`shrink-0 select-none ${className}`}
    />
  );
}
