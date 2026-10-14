import * as React from 'react';
import { LucideIcon, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { cn } from '@/lib/utils';

interface DashboardCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  change?: string;
  trend?: 'up' | 'down' | 'neutral';
  description?: string;
  className?: string;
}

export function DashboardCard({
  title,
  value,
  icon: Icon,
  change,
  trend = 'up',
  description,
  className,
}: DashboardCardProps) {
  return (
    <div
      className={cn(
        'bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs flex flex-col justify-between transition-all hover:border-neutral-300',
        className
      )}
    >
      <div className="flex items-center justify-between gap-4">
        <span className="text-xs font-semibold uppercase tracking-wider text-neutral-500">
          {title}
        </span>
        <div className="p-2.5 rounded-xl bg-neutral-100 text-neutral-800 shrink-0">
          <Icon className="size-5 text-neutral-700" />
        </div>
      </div>

      <div className="mt-4 space-y-2">
        <p className="text-2xl sm:text-3xl font-bold text-neutral-900 tracking-tight font-display">
          {value}
        </p>

        {(change || description) && (
          <div className="flex items-center gap-2 text-xs">
            {change && (
              <span
                className={cn(
                  'inline-flex items-center gap-0.5 font-semibold px-1.5 py-0.5 rounded-md text-[11px]',
                  trend === 'up'
                    ? 'bg-emerald-50 text-emerald-700'
                    : trend === 'down'
                    ? 'bg-rose-50 text-rose-700'
                    : 'bg-neutral-100 text-neutral-600'
                )}
              >
                {trend === 'up' && <ArrowUpRight className="size-3" />}
                {trend === 'down' && <ArrowDownRight className="size-3" />}
                {change}
              </span>
            )}
            {description && (
              <span className="text-neutral-500">{description}</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

