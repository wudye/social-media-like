// src/stores/blogSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { blogApi } from '../api/services';

// 初始状态
const initialState = {
  blogs: [],          // 博客列表
  blogDetails: {},    // 博客详情缓存
  loading: false,     // 加载状态
  isLoaded: false,    // 是否已加载
};

// 异步 Action：获取博客列表
export const fetchBlogs = createAsyncThunk(
  'blog/fetchBlogs',
  async (_, { getState }) => {
    const { blog } = getState();
    if (blog.isLoaded && blog.blogs.length > 0) {
      return blog.blogs;
    }

    const response = await blogApi.getList();
    if (response.data && response.data.code === 0) {
      return response.data.data || [];
    }
    throw new Error('Failed to fetch blogs');
  }
);

// 异步 Action：获取博客详情
export const fetchBlogDetail = createAsyncThunk(
  'blog/fetchBlogDetail',
  async (blogId) => {
    const response = await blogApi.getDetail(blogId);
    if (response.data && response.data.code === 0 && response.data.data) {
      return { blogId, blog: response.data.data };
    }
    throw new Error('Failed to fetch blog detail');
  }
);

// 创建 Slice
const blogSlice = createSlice({
  name: 'blog',
  initialState,
  reducers: {
    // 记录博客访问历史
    addToHistory: (state, action) => {
      const blogId = action.payload;
      const history = JSON.parse(localStorage.getItem('blogHistory') || '[]');
      
      // 移除旧记录（如果存在）
      const index = history.findIndex(id => id === blogId);
      if (index !== -1) history.splice(index, 1);
      
      // 添加到最前面并保留最近10条
      const recentHistory = [blogId, ...history].slice(0, 10);
      localStorage.setItem('blogHistory', JSON.stringify(recentHistory));
    },
    // 更新点赞状态
    updateBlogThumbStatus: (state, action) => {
      const { blogId, hasThumb } = action.payload;
      const stringId = String(blogId);

      // 更新详情缓存
      if (state.blogDetails[stringId]) {
        const blog = state.blogDetails[stringId];
        blog.hasThumb = hasThumb;
        blog.thumbCount = hasThumb ? blog.thumbCount + 1 : Math.max(0, blog.thumbCount - 1);
      }

      // 更新列表
      const blogIndex = state.blogs.findIndex(blog => String(blog.id) === stringId);
      if (blogIndex !== -1) {
        const blog = state.blogs[blogIndex];
        blog.hasThumb = hasThumb;
        blog.thumbCount = hasThumb ? blog.thumbCount + 1 : Math.max(0, blog.thumbCount - 1);
      }
    },
  },
  extraReducers: (builder) => {
    builder
      // 处理 fetchBlogs 的 loading 状态
      .addCase(fetchBlogs.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchBlogs.fulfilled, (state, action) => {
        state.blogs = action.payload;
        state.isLoaded = true;
        state.loading = false;
      })
      .addCase(fetchBlogs.rejected, (state) => {
        state.loading = false;
      })
      // 处理 fetchBlogDetail 的 loading 状态
      .addCase(fetchBlogDetail.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchBlogDetail.fulfilled, (state, action) => {
        const { blogId, blog } = action.payload;
        state.blogDetails[String(blogId)] = blog;
        state.loading = false;
      })
      .addCase(fetchBlogDetail.rejected, (state) => {
        state.loading = false;
      });
  },
});

// Selectors
export const selectBlogs = (state) => state.blog.blogs;
export const selectBlogDetails = (state) => state.blog.blogDetails;
export const selectLoading = (state) => state.blog.loading;
export const selectIsLoaded = (state) => state.blog.isLoaded;
export const getBlogById = (state, id) => {
  const stringId = String(id);
  return state.blog.blogDetails[stringId] || 
         state.blog.blogs.find(blog => String(blog.id) === stringId);
};
export const getRecentHistory = () => {
  return JSON.parse(localStorage.getItem('blogHistory') || '[]');
};

// 导出 actions 和 reducer
export const { addToHistory, updateBlogThumbStatus } = blogSlice.actions;
export default blogSlice.reducer;
