export * from '@/types/inventory-sale';

export interface CountdownTime {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
  isEnded: boolean;
  isUpcoming: boolean;
}
