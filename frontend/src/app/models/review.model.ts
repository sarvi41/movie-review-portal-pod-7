export interface Review {
  id: string;
  userId: string;
  userName: string;
  movieId: string;
  reviewText: string;
  rating: number;
  createdAt: string;
}

export interface ReviewPage {
  page: number;
  size: number;
  total: number;
  reviews: Review[];
}

export interface ReviewRequest {
  reviewText: string;
  rating?: number;
}
