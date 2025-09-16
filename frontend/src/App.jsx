import { BrowserRouter, createBrowserRouter, RouterProvider } from 'react-router-dom';


import Home from './views/Home';
import BlogDetail from './views/BlogDetail';
import Login from './views/Login';
import NotFound from './views/NotFound';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />,
  },
  {
    path: '/blog/:id',
    element: <BlogDetail />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '*',
    element: <NotFound />,
  },
]);

function App() {

  return (
    <>
      <RouterProvider router={router} />
    </>
  )
}

export default App
