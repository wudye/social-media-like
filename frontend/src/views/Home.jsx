// src/views/Home.jsx
import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchBlogs } from '../store/blogSlice';
import {  restoreLogin } from '../store/userSlice';
import ThumbButton from '../components/ThumbButton';
import LoginBadge from '../components/common/LoginBadge';
import ApiLoader from '../components/common/ApiLoader';
import { formatDate, truncateString } from '../utils';

const Home = () => {
  const dispatch = useDispatch();
  const { blogs, loading } = useSelector((state) => state.blog);

  // 获取博客列表
  const fetchBlogsData = async () => {
    await dispatch(fetchBlogs());
  };

  // 初始化
  useEffect(() => {
    const initialize = async () => {
      await dispatch(restoreLogin());
      await fetchBlogsData();
    };
    initialize();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container-custom py-8">
        <div className="page-header flex justify-between items-center mb-8">
          <h1 className="page-title text-2xl font-bold text-gray-800">chosen blogs</h1>
          <LoginBadge />
        </div>

        <ApiLoader loading={loading} fullscreen={true} />

        {!loading && blogs.length === 0 ? (
          <div className="empty-state flex items-center justify-center py-16 text-gray-500 text-lg">
            <p>no blogs</p>
          </div>
        ) : (
          <div className="blog-grid grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {blogs.map((blog) => (
              <div key={blog.id} className="blog-card-container flex flex-col h-full transition-transform duration-300 hover:-translate-y-1">
                <Link
                  to={`/blog/${blog.id}`}
                  className="blog-content-link block no-underline text-inherit transition-colors"
                >
                  <div className="blog-cover w-full overflow-hidden h-[200px] flex items-center justify-center bg-gray-100">
                    <img
                      src={blog.coverImg}
                      alt={blog.title}
                      className="cover-img w-full h-full object-contain transition-transform duration-500 hover:scale-105"
                      referrerPolicy="no-referrer"
                    />
                  </div>
                  <div className="blog-content flex flex-col flex-grow p-4">
                    <h3 className="blog-title text-lg font-semibold text-gray-800 mb-2 line-clamp-2">
                      {blog.title}
                    </h3>
                    <p className="blog-summary text-sm text-gray-600 mb-4 line-clamp-3">
                      {truncateString(blog.content, 100)}
                    </p>
                  </div>
                </Link>

                <div className="blog-meta flex justify-between items-center p-4 pt-0 mt-auto">
                  <div className="blog-time text-xs text-gray-500">
                    {formatDate(blog.createTime)}
                  </div>
                  <ThumbButton blogId={blog.id} count={blog.thumbCount} hasThumb={blog.hasThumb} />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
