import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';

import Anonymize from './Anonymize';
import ConfigPage from './Config';
import Source from './Source';
import SourceNext from './SourceNext';

function App() {

  return (
    <Router>
      <Routes>
        <Route exact path="/" element={<Source />} />
        <Route path="/source" element={<Source />} />
        <Route path="/sourcenext" element={<SourceNext />} />
        <Route path="/config" element={<ConfigPage />} />
        <Route path="/anonymizer" element={<Anonymize />} />
      </Routes>
    </Router>
  );
}

export default App;