export const getUserId = (state) => state.user.currentUser?._id;
export const getUsername = (state) => state.user.currentUser?.username;