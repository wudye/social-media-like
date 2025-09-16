// userSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {userApi} from '../api/services';

// Async thunks
export const login = createAsyncThunk(
  'user/login',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await userApi.login(userId);
      if (response.data && response.data.code === 0 && response.data.data) {
        localStorage.setItem('userId', userId);
        return response.data.data;
      }
      return rejectWithValue('Login failed');
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const fetchCurrentUser = createAsyncThunk(
  'user/fetchCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const response = await userApi.getLoginUser();
      if (response.data && response.data.code === 0 && response.data.data) {
        return response.data.data;
      }
      return rejectWithValue('Fetch failed');
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const restoreLogin = createAsyncThunk(
  'user/restoreLogin',
  async (_, { dispatch }) => {
    const userId = localStorage.getItem('userId');
    if (userId) {
      return await dispatch(login(userId)).unwrap();
    }
    throw new Error('No userId found');
  }
);

const initialState = {
  currentUser: null,
  isLoggedIn: false,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearUser(state) {
      state.currentUser = null;
      state.isLoggedIn = false;
      localStorage.removeItem('userId');
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isLoggedIn = true;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isLoggedIn = true;
      })
      .addCase(restoreLogin.fulfilled, (state, action) => {
        state.currentUser = action.payload;
        state.isLoggedIn = true;
      });
  },
});

export const { clearUser } = userSlice.actions;
export default userSlice.reducer;