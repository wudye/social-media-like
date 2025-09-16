// src/stores/thumbSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { thumbApi } from '../api/services';

// 初始状态
const initialState = {
  thumbedBlogs: new Set(), // 用户已点赞的博客ID集合
  isLoading: false,        // 点赞操作状态
};

// 异步 Action：点赞博客
export const doThumb = createAsyncThunk(
  'thumb/doThumb',
  async (blogId, { dispatch }) => {
    try {
      console.log('开始点赞操作', blogId);
      const response = await thumbApi.doThumb(blogId);

      if (response.data && response.data.code === 0 && response.data.data === true) {
        console.log('点赞成功', blogId);
        return blogId; // 返回点赞成功的博客ID
      }
      console.log('点赞失败', response.data);
      throw new Error('点赞失败');
    } catch (error) {
      console.error('点赞操作异常', error);
      throw error;
    }
  }
);

// 异步 Action：取消点赞
export const undoThumb = createAsyncThunk(
  'thumb/undoThumb',
  async (blogId, { dispatch }) => {
    try {
      console.log('开始取消点赞操作', blogId);
      const response = await thumbApi.undoThumb(blogId);

      if (response.data && response.data.code === 0 && response.data.data === true) {
        console.log('取消点赞成功', blogId);
        return blogId; // 返回取消点赞的博客ID
      }
      console.log('取消点赞失败', response.data);
      throw new Error('取消点赞失败');
    } catch (error) {
      console.error('取消点赞操作异常', error);
      throw error;
    }
  }
);

// 创建 Slice
const thumbSlice = createSlice({
  name: 'thumb',
  initialState,
  reducers: {
    // 设置博客的点赞状态
    setThumbStatus: (state, action) => {
      const { blogId, status } = action.payload;
      if (status) {
        state.thumbedBlogs.add(String(blogId));
      } else {
        state.thumbedBlogs.delete(String(blogId));
      }
    },
    // 批量设置点赞状态
    setMultipleThumbStatus: (state, action) => {
      const blogs = action.payload;
      if (!blogs || !blogs.length) return;

      blogs.forEach(blog => {
        if (blog.hasThumb) {
          state.thumbedBlogs.add(String(blog.id));
        } else {
          state.thumbedBlogs.delete(String(blog.id));
        }
      });
    },
  },
  extraReducers: (builder) => {
    builder
      // 处理 doThumb 的 loading 状态
      .addCase(doThumb.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(doThumb.fulfilled, (state, action) => {
        state.thumbedBlogs.add(String(action.payload));
        state.isLoading = false;
      })
      .addCase(doThumb.rejected, (state) => {
        state.isLoading = false;
      })
      // 处理 undoThumb 的 loading 状态
      .addCase(undoThumb.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(undoThumb.fulfilled, (state, action) => {
        state.thumbedBlogs.delete(String(action.payload));
        state.isLoading = false;
      })
      .addCase(undoThumb.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

// Selectors
export const selectThumbedBlogs = (state) => state.thumb.thumbedBlogs;
export const selectIsLoading = (state) => state.thumb.isLoading;
export const hasThumb = (state, blogId) => state.thumb.thumbedBlogs.has(String(blogId));

// 导出 actions 和 reducer
export const { setThumbStatus, setMultipleThumbStatus } = thumbSlice.actions;
export default thumbSlice.reducer;
