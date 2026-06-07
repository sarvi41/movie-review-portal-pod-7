export interface Movie {
  id: string;
  title: string;
  releaseYear: number;
  description: string;
  director: string;
  runtimeMinutes: number;
  posterUrl?: string;
  averageRating: number;
  genres: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface MoviePage {
  page: number;
  size: number;
  total: number;
  movies: Movie[];
}
