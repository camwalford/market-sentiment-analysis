import UserData from "./UserData";

type LoginResponse = {
    accessToken: string;
    refreshToken: string;
    user: UserData;
};

export default LoginResponse;