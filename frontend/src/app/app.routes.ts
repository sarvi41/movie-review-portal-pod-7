import { Routes } from "@angular/router";
import { LoginComponent } from "./components/login/login.component";
import { MovieDetailsComponent } from "./components/movie-details/movie-details.component";
import { MovieListComponent } from "./components/movie-list/movie-list.component";
import { ProfileComponent } from "./components/profile/profile.component";
import { ReviewFormComponent } from "./components/review-form/review-form.component";

export const appRoutes: Routes = [
  { path: "", redirectTo: "movies", pathMatch: "full" },
  { path: "login", component: LoginComponent },
  { path: "movies", component: MovieListComponent },
  { path: "movies/:id/review", component: ReviewFormComponent },
  { path: "movies/:id", component: MovieDetailsComponent },
  { path: "profile", component: ProfileComponent },
  { path: "**", redirectTo: "movies" },
];
