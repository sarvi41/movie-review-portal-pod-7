import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { ReviewService } from "../../services/review.service";
import { ReviewRequest } from "../../models/review.model";

@Component({
  selector: "app-review-form",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: "./review-form.component.html",
  styleUrls: ["./review-form.component.scss"],
})
export class ReviewFormComponent implements OnInit {
  form = this.fb.group({
    reviewText: [
      "",
      [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(2000),
      ],
    ],
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
  });
  movieId = "";

  constructor(
    private readonly fb: FormBuilder,
    private readonly reviewService: ReviewService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.movieId = this.route.snapshot.paramMap.get("id") || "";
  }

  get reviewText() {
    return this.form.get("reviewText");
  }

  get rating() {
    return this.form.get("rating");
  }

  onSubmit(): void {
    if (!this.movieId || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.reviewService
      .submitReview(this.movieId, this.form.value as ReviewRequest)
      .subscribe({
        next: () => {
          this.snackBar.open("Review submitted successfully.", "Close", {
            duration: 3000,
          });
          this.router.navigate(["/movies", this.movieId]);
        },
        error: (error) => {
          this.snackBar.open(
            error.message || "Unable to submit review.",
            "Dismiss",
            { duration: 5000 },
          );
        },
      });
  }
}
