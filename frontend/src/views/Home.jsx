// src/views/Home.jsx
import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchBlogs } from '../store/blogSlice';
import { restoreLogin } from '../store/userSlice';
import ThumbButton from '../components/ThumbButton';
import LoginBadge from '../components/common/LoginBadge';
import ApiLoader from '../components/common/ApiLoader';
import { formatDate, truncateString } from '../utils';

const Home = () => {
  const dispatch = useDispatch();
  const { blogs, loading } = useSelector((state) => state.blog);
  const { user } = useSelector((state) => state.user);

  useEffect(() => {
    dispatch(restoreLogin());
    dispatch(fetchBlogs());
  }, [dispatch]);

  return (
    <div className="home-page">
      <div className="container-custom py-8">
        <div className="page-header">
          <h1 className="page-title">精选博客</h1>
          <LoginBadge user={user} />
        </div>

        <ApiLoader loading={loading} fullscreen={true} />

        {!loading && blogs.length === 0 ? (
          <div className="empty-state">
            <p>暂无博客内容</p>
          </div>
        ) : (
          <div className="blog-grid">
            {blogs.map((blog) => (
              <div key={blog.id} className="blog-card-container card">
                <a href={`/blog/${blog.id}`} className="blog-content-link">
                  <div className="blog-cover">
                    <img
                      src={blog.coverImg}
                      alt={blog.title}
                      className="cover-img"
                      referrerPolicy="no-referrer"
                    />
                  </div>
                  <div className="blog-content">
                    <h3 className="blog-title">{blog.title}</h3>
                    <p className="blog-summary">{truncateString(blog.content, 100)}</p>
                  </div>
                </a>
                <div className="blog-meta">
                  <div className="blog-time">{formatDate(blog.createTime)}</div>
                  <ThumbButton
                    blogId={blog.id}
                    count={blog.thumbCount}
                    hasThumb={blog.hasThumb}
                  />
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
