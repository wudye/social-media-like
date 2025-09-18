// src/components/common/LoginBadge.jsx
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { clearUser } from '../../store/userSlice';

const LoginBadge = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isLoggedIn, username, userId } = useSelector((state) => state.user);

  useEffect(() => {
    console.log('LoginBadge组件加载，当前用户状态:', {
      isLoggedIn,
      username,
      userId,
    });
  }, [isLoggedIn, username, userId]);

  const goToLogin = () => {
    navigate('/login');
  };

  const handleLogout = () => {
    dispatch(clearUser());
    navigate('/');
  };

  return (
    <div className="flex items-center">
      {isLoggedIn ? (
        <div className="flex items-center gap-2">
          <span className="text-gray-800 font-medium">
            {username || `用户${userId}`}
          </span>
          <button
            onClick={handleLogout}
            className="text-sm text-red-500 hover:text-red-500 transition-colors"
          >
            exit
          </button>
        </div>
      ) : (
        <button
          onClick={goToLogin}
          className="btn btn-primary text-sm py-1 px-3 rounded-md bg-red-500"
        >
          login
        </button>
      )}
    </div>
  );
};

export default LoginBadge;
