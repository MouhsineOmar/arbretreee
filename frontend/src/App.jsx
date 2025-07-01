import React, { useState } from 'react';
import './App.css';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './components/ui/tabs';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './components/ui/card';
import DataUpload from './components/DataUpload';
import ModelTraining from './components/ModelTraining';
import Results from './components/Results';
import { TreePine, Upload, Brain, BarChart3 } from 'lucide-react';
import logoImage from './assets/lo.jpg';

function App() {
  const [uploadedData, setUploadedData] = useState(null);
  const [trainingResults, setTrainingResults] = useState(null);
  const [activeTab, setActiveTab] = useState('upload');

  const handleDataUploaded = (data) => {
    setUploadedData(data);
    setActiveTab('training');
  };

  const handleModelTrained = (results) => {
    setTrainingResults(results);
    setActiveTab('results');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800">
      {/* Header */}
      <header className="bg-white dark:bg-gray-800 shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-3">
              <img 
                src={logoImage} 
                alt="Logo" 
                className="h-10 w-10 rounded-full object-cover"
              />
              <div>
                <h1 className="text-xl font-bold text-gray-900 dark:text-white">
                  Arbres de Décision
                </h1>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Machine Learning & Classification
                </p>
              </div>
            </div>
            <TreePine className="h-8 w-8 text-green-600" />
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Card className="w-full">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <Brain className="h-6 w-6" />
              <span>Application d'Arbres de Décision</span>
            </CardTitle>
            <CardDescription>
              Chargez vos données, entraînez un modèle et analysez les résultats
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="upload" className="flex items-center space-x-2">
                  <Upload className="h-4 w-4" />
                  <span>Données</span>
                </TabsTrigger>
                <TabsTrigger 
                  value="training" 
                  disabled={!uploadedData}
                  className="flex items-center space-x-2"
                >
                  <Brain className="h-4 w-4" />
                  <span>Entraînement</span>
                </TabsTrigger>
                <TabsTrigger 
                  value="results" 
                  disabled={!trainingResults}
                  className="flex items-center space-x-2"
                >
                  <BarChart3 className="h-4 w-4" />
                  <span>Résultats</span>
                </TabsTrigger>
              </TabsList>

              <TabsContent value="upload" className="mt-6">
                <DataUpload onDataUploaded={handleDataUploaded} />
              </TabsContent>

              <TabsContent value="training" className="mt-6">
                <ModelTraining 
                  uploadedData={uploadedData} 
                  onModelTrained={handleModelTrained} 
                />
              </TabsContent>

              <TabsContent value="results" className="mt-6">
                <Results trainingResults={trainingResults} />
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}

export default App;

