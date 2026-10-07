import React from 'react';
import './App.css';
import Header from './components/Header';
import StudentProfile from './pages/StudentProfile';

function App() {
  return (
      <div className="App">
        <Header />
        <StudentProfile />
      </div>
  );
}

export default App;