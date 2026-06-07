import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, throwError } from "rxjs";
import { environment } from "../../environments/environment";
import { Review, ReviewPage, ReviewRequest } from "../models/review.model";

@Injectable({ providedIn: "root" })
export class ReviewService {
  constructor(private readonly http: HttpClient) {}

  loadReviews(movieId: string, page = 1, size = 10): Observable<ReviewPage> {
    const params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());
    return this.http
      .get<ReviewPage>(`${environment.apiUrl}/movies/${movieId}/reviews`, {
        params,
      })
      .pipe(catchError((error) => this.handleError(error)));
  }

  submitReview(movieId: string, payload: ReviewRequest): Observable<Review> {
    return this.http
      .post<Review>(`${environment.apiUrl}/movies/${movieId}/reviews`, payload)
      .pipe(catchError((error) => this.handleError(error)));
  }

  private handleError(error: HttpErrorResponse) {
    const message =
      error.error?.message || error.statusText || "Unable to submit review.";
    return throwError(() => new Error(message));
  }
}
