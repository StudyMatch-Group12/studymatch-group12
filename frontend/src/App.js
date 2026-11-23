import React, { useState } from 'react';
import './App.css';
import './pages/Students.css';
import RegistoEstudante from './components/RegistoEstudante';
import StudentList from './pages/StudentList';
import StudentProfile from './pages/StudentProfile';
import GroupingContexts from './pages/GroupingContexts';
import GroupResults from './pages/GroupResults';

const PAGES = {
    REGISTER: 'register',
    STUDENTS: 'students',
    PROFILE: 'profile',
    GROUPING: 'grouping',
};

function App() {
    const [page, setPage] = useState(PAGES.REGISTER);
    const [selectedStudentId, setSelectedStudentId] = useState(null);
    const [selectedContextId, setSelectedContextId] = useState(null);

    const openProfile = (studentId) => {
        setSelectedStudentId(studentId);
        setPage(PAGES.PROFILE);
    };

    const openContext = (contextId) => {
        setSelectedContextId(contextId);
    };

    return (
        <div className="App">
            <nav className="app-nav">
                <button
                    type="button"
                    className={page === PAGES.REGISTER ? 'active' : ''}
                    onClick={() => setPage(PAGES.REGISTER)}
                >
                    Register
                </button>
                <button
                    type="button"
                    className={
                        page === PAGES.STUDENTS ||
                        page === PAGES.PROFILE
                            ? 'active' : ''
                    }
                    onClick={() => setPage(PAGES.STUDENTS)}
                >
                    Students
                </button>
                <button
                    type="button"
                    className={page === PAGES.GROUPING ? 'active' : ''}
                    onClick={() => setPage(PAGES.GROUPING)}
                >
                    Grupos
                </button>
            </nav>

            {page === PAGES.REGISTER && <RegistoEstudante />}
            {page === PAGES.STUDENTS && (
                <StudentList onSelectStudent={openProfile} />
            )}
            {page === PAGES.PROFILE && (
                <StudentProfile
                    studentId={selectedStudentId}
                    onBack={() => setPage(PAGES.STUDENTS)}
                />
            )}
            {page === PAGES.GROUPING && (
                <GroupingContexts
                    onSelectContext={openContext}
                />
            )}
            {selectedContextId !== null && page === PAGES.GROUPING && (
                <GroupResults contextId={selectedContextId} onSelectStudent={openProfile} />
            )}
        </div>
    );
}

export default App;
