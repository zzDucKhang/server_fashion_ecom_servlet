import * as React from 'react';
import { cn } from '@/lib/utils';

interface AvatarProps extends React.HTMLAttributes<HTMLDivElement> {
  src?: string | null;
  alt?: string;
  fallback?: string;
  size?: 'sm' | 'default' | 'lg' | 'xl';
}

export function Avatar({
  src,
  alt = 'Avatar',
  fallback,
  size = 'default',
  className,
  ...props
}: AvatarProps) {
  const [imageError, setImageError] = React.useState(false);

  const sizeClasses = {
    sm: 'size-7 text-xs',
    default: 'size-9 text-xs',
    lg: 'size-12 text-sm',
    xl: 'size-16 text-base font-medium',
  };

  const getFallbackText = () => {
    if (fallback) return fallback.slice(0, 2).toUpperCase();
    if (alt) return alt.slice(0, 2).toUpperCase();
    return 'U';
  };

  return (
    <div
      className={cn(
        'relative inline-flex shrink-0 items-center justify-center overflow-hidden rounded-full bg-neutral-200 text-neutral-700 font-semibold select-none ring-1 ring-black/5',
        sizeClasses[size],
        className
      )}
      {...props}
    >
      {src && !imageError ? (
        <img
          src={src}
          alt={alt}
          onError={() => setImageError(true)}
          className="size-full object-cover"
        />
      ) : (
        <span>{getFallbackText()}</span>
      )}
    </div>
  );
}

