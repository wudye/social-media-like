// src/store.js
import { configureStore } from '@reduxjs/toolkit';
import blogReducer from './blogSlice';
import userReducer from './userSlice';

const store = configureStore({
  reducer: {
    blog: blogReducer,
    user: userReducer,
  },
});

export default store;
