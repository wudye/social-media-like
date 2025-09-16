// src/components/BlogCard.jsx
import { formatDate, truncateString } from '../utils';
import ThumbButton from './ThumbButton';

const BlogCard = ({ blog }) => {
  // 计算摘要信息（截取内容前100个字符）
  const summary = truncateString(blog.content, 100);
  // 格式化时间
  const formattedTime = formatDate(blog.createTime);

  return (
    <div className="blog-card card">
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
        <p className="blog-summary">{summary}</p>
        <div className="blog-meta">
          <div className="blog-time">{formattedTime}</div>
          <ThumbButton
            blogId={blog.id}
            count={blog.thumbCount}
            hasThumb={blog.hasThumb}
          />
        </div>
      </div>
    </div>
  );
};

export default BlogCard;
