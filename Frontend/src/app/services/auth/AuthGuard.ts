import { inject } from "@angular/core";
import { UserService } from "../user/user.service";
import { Router } from "@angular/router";

export const AuthGuard = () => {
    const userService = inject(UserService);
    const router = inject(Router);

    if(userService.isLogged()) {
        return true;
    } else {
        router.navigate(['/']);
        return false;
    }
}