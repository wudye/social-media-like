import { configureStore } from '@reduxjs/toolkit';
import blogReducer from './blogSlice';
import userReducer from './userSlice';
import thumbReducer from './thumbSlice';

const store = configureStore({
  reducer: {
    user: userReducer,
    blog: blogReducer,
    thumb: thumbReducer,
  },
});

export default store;
