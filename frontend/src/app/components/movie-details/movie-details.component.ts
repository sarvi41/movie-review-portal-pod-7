import { CommonModule } from "@angular/common";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, RouterModule } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatChipsModule } from "@angular/material/chips";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatListModule } from "@angular/material/list";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { Subscription } from "rxjs";
import { Movie } from "../../models/movie.model";
import { Review, ReviewPage } from "../../models/review.model";
import { MovieService } from "../../services/movie.service";
import { ReviewService } from "../../services/review.service";

@Component({
  selector: "app-movie-details",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatChipsModule,
    MatDividerModule,
    MatIconModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: "./movie-details.component.html",
  styleUrls: ["./movie-details.component.scss"],
})
export class MovieDetailsComponent implements OnInit, OnDestroy {
  movie: Movie | null = null;
  reviews: Review[] = [];
  reviewPage: ReviewPage | null = null;
  loading = false;
  reviewLoading = false;
  private readonly subscription = new Subscription();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly movieService: MovieService,
    private readonly reviewService: ReviewService,
    private readonly snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.subscription.add(
      this.route.paramMap.subscribe((params) => {
        const movieId = params.get("id");
        if (movieId) {
          this.fetchMovie(movieId);
          this.fetchReviews(movieId);
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private fetchMovie(movieId: string): void {
    this.loading = true;
    this.movieService.loadMovie(movieId).subscribe({
      next: (movie) => {
        this.movie = movie;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open(
          error.message || "Unable to load movie.",
          "Dismiss",
          { duration: 5000 },
        );
      },
    });
  }

  private fetchReviews(movieId: string): void {
    this.reviewLoading = true;
    this.reviewService.loadReviews(movieId, 1, 10).subscribe({
      next: (page) => {
        this.reviewPage = page;
        this.reviews = page.reviews;
        this.reviewLoading = false;
      },
      error: (error) => {
        this.reviewLoading = false;
        this.snackBar.open(
          error.message || "Unable to load reviews.",
          "Dismiss",
          { duration: 5000 },
        );
      },
    });
  }
}
