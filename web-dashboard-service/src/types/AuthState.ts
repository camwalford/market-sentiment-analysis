import UserData from './UserData';

type AuthState = {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserData | null;
};
export default AuthState;