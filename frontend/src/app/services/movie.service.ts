import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, catchError, Observable, throwError } from "rxjs";
import { environment } from "../../environments/environment";
import { Movie } from "../models/movie.model";

@Injectable({ providedIn: "root" })
export class MovieService {
  private readonly moviesSubject = new BehaviorSubject<Movie[]>([]);
  movies$ = this.moviesSubject.asObservable();

  private readonly selectedMovieSubject = new BehaviorSubject<Movie | null>(
    null,
  );
  selectedMovie$ = this.selectedMovieSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  loadMovies(): Observable<Movie[]> {
    return this.http
      .get<Movie[]>(`${environment.apiUrl}/movies`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  refreshMovies(): void {
    this.loadMovies().subscribe({
      next: (movies) => this.moviesSubject.next(movies),
      error: () => this.moviesSubject.next([]),
    });
  }

  loadMovie(movieId: string): Observable<Movie> {
    return this.http
      .get<Movie>(`${environment.apiUrl}/movies/${movieId}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  selectMovie(movie: Movie | null): void {
    this.selectedMovieSubject.next(movie);
  }

  private handleError(error: HttpErrorResponse) {
    const message =
      error.error?.message || error.statusText || "Unable to load movies.";
    return throwError(() => new Error(message));
  }
}
