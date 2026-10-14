export type Product = {
  slug: string;
  name: string;
  brand: string;
  category: string;
  price: number;
  originalPrice?: number;
  flashSalePrice?: number;
  rating: number;
  reviews: number;
  tag?: string;
  images: string[];
  colors: string[];
  sizes: string[];
  description: string;
};
const image = (id: string, w = 900, h = 1100) =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${w}&h=${h}&q=85`;
export const categories = [
  {
    name: 'Apparel',
    count: 48,
    icon: '✦',
    image: image('photo-1496747611176-843222e1e57c'),
  },
  {
    name: 'Objects',
    count: 32,
    icon: '◒',
    image: image('photo-1494438639946-1ebd1d20bf85'),
  },
  {
    name: 'Accessories',
    count: 26,
    icon: '◇',
    image: image('photo-1525507119028-ed4c629a60a3'),
  },
  {
    name: 'Wellness',
    count: 18,
    icon: '⌁',
    image: image('photo-1608571423902-eed4a5ad8108'),
  },
  {
    name: 'Stationery',
    count: 14,
    icon: '▱',
    image: image('photo-1455390582262-044cdead277a'),
  },
  {
    name: 'Home',
    count: 22,
    icon: '⌂',
    image: image('photo-1618221195710-dd6b41faaea6'),
  },
];
export const products: Product[] = [
  {
    slug: 'linen-overshirt',
    name: 'Linen Overshirt',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 168,
    originalPrice: 210,
    flashSalePrice: 168,
    rating: 4.9,
    reviews: 86,
    tag: 'Bestseller',
    images: [
      image('photo-1594633312681-425c7b97ccd1'),
      image('photo-1529139574466-a303027c1d8b'),
    ],
    colors: ['#ded5c8', '#1D1F20', '#bec9bf'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'An easy, everyday layer cut from breathable European linen. Relaxed through the body with a clean, considered finish.',
  },
  {
    slug: 'everyday-tote',
    name: 'Everyday Tote',
    brand: 'Form & Field',
    category: 'Accessories',
    price: 92,
    rating: 4.8,
    reviews: 42,
    tag: 'New',
    images: [
      image('photo-1548036328-c9fa89d128fa'),
      image('photo-1553062407-98eeb64c6a62'),
    ],
    colors: ['#b8794b', '#242424', '#ded5c8'],
    sizes: ['One size'],
    description:
      'A generous, structured canvas tote with vegetable-tanned leather handles and room for all your daily rituals.',
  },
  {
    slug: 'ribbed-knit-set',
    name: 'Ribbed Knit Set',
    brand: 'House of Common',
    category: 'Apparel',
    price: 144,
    originalPrice: 180,
    flashSalePrice: 144,
    rating: 4.7,
    reviews: 31,
    tag: '20% off',
    images: [
      image('photo-1483985988355-763728e1935b'),
      image('photo-1515886657613-9f3515b0c78f'),
    ],
    colors: ['#c8b8ad', '#5b6353'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'Soft, tactile knitwear made to move through the week. A matching set that does more with less.',
  },
  {
    slug: 'stoneware-mug',
    name: 'Stoneware Mug',
    brand: 'Kiln Notes',
    category: 'Objects',
    price: 34,
    rating: 4.9,
    reviews: 112,
    images: [
      image('photo-1514228742587-6b1558fcca3d'),
      image('photo-1577937927133-66ef06acdf18'),
    ],
    colors: ['#d8c5ac', '#b9c8c2', '#202121'],
    sizes: ['One size'],
    description:
      'Hand-finished stoneware with a generous handle and a quiet, matte glaze. Each one is subtly unique.',
  },
  {
    slug: 'pleated-trouser',
    name: 'Pleated Trouser',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 128,
    rating: 4.6,
    reviews: 24,
    images: [
      image('photo-1506629905607-d9b1f1f3d7a8'),
      image('photo-1473966968600-fa801b869a1a'),
    ],
    colors: ['#282a2a', '#ded7c9'],
    sizes: ['28', '30', '32', '34'],
    description:
      'A high-waisted trouser with a softly tailored line, finished in a fluid, mid-weight cotton blend.',
  },
  {
    slug: 'daily-journal',
    name: 'Daily Journal',
    brand: 'Paper Sunday',
    category: 'Stationery',
    price: 28,
    rating: 4.8,
    reviews: 67,
    tag: 'Staff pick',
    images: [
      image('photo-1517842645767-c639042777db'),
      image('photo-1499951360447-b19be8fe80f5'),
    ],
    colors: ['#d8c9b8', '#8d9a87', '#2c3430'],
    sizes: ['A5'],
    description:
      'A calm place for plans, notes, and half-formed ideas. 160 pages of smooth, fountain-pen friendly paper.',
  },
  {
    slug: 'everyday-sandal',
    name: 'Everyday Sandal',
    brand: 'Lumen Goods',
    category: 'Accessories',
    price: 76,
    rating: 4.7,
    reviews: 18,
    images: [
      image('photo-1543163521-1bf539c55dd2'),
      image('photo-1560343090-f0409e92791a'),
    ],
    colors: ['#c8895b', '#1f2321'],
    sizes: ['36', '37', '38', '39', '40'],
    description:
      'A minimal leather sandal with a cushioned footbed, designed for long walks and slow afternoons.',
  },
  {
    slug: 'linen-candle',
    name: 'Linen + Cedar Candle',
    brand: 'Sunday Objects',
    category: 'Wellness',
    price: 42,
    rating: 4.9,
    reviews: 53,
    images: [
      image('photo-1603006905003-be475563bc59'),
      image('photo-1602523961358-f9f03dd557db'),
    ],
    colors: ['#e4dac8'],
    sizes: ['220g'],
    description:
      'A warm, grounding blend of washed linen, cedar, and a little smoke. Poured by hand in small batches.',
  },
];
export const getProduct = (slug: string) =>
  products.find((product) => product.slug === slug) ?? products[0];
