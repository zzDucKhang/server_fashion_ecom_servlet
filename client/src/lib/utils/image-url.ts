export const DEFAULT_IMAGE_URL =
  'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80';

export function normalizeImageUrl(imageUrl?: string | null, fallback = DEFAULT_IMAGE_URL) {
  if (!imageUrl) return fallback;

  const trimmedUrl = imageUrl.trim();
  if (!trimmedUrl) return fallback;

  try {
    const parsedUrl = new URL(trimmedUrl);

    if (
      (parsedUrl.hostname === 'www.google.com' || parsedUrl.hostname === 'google.com') &&
      parsedUrl.pathname.includes('/imgres')
    ) {
      const extractedImageUrl = parsedUrl.searchParams.get('imgurl');
      if (extractedImageUrl) {
        return decodeURIComponent(extractedImageUrl);
      }
    }

    return trimmedUrl;
  } catch {
    if (trimmedUrl.startsWith('/') || trimmedUrl.startsWith('data:')) {
      return trimmedUrl;
    }

    return fallback;
  }
}