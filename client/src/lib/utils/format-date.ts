import { format, parseISO } from 'date-fns';

export function formatDate(dateString: string | undefined | null, pattern = 'dd/MM/yyyy HH:mm'): string {
  if (!dateString) return '';
  try {
    return format(parseISO(dateString), pattern);
  } catch {
    return dateString;
  }
}