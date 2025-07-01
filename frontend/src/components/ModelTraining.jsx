import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Label } from './ui/label';
import { Input } from './ui/input';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';
import { Alert, AlertDescription } from './ui/alert';
import { Progress } from './ui/progress';
import { Brain, Play, CheckCircle, AlertCircle, Settings } from 'lucide-react';

const ModelTraining = ({ uploadedData, onModelTrained }) => {
  const [algorithm, setAlgorithm] = useState('ID3');
  const [numberOfTrees, setNumberOfTrees] = useState(100);
  const [training, setTraining] = useState(false);
  const [error, setError] = useState(null);
  const [progress, setProgress] = useState(0);

  const algorithms = [
    {
      id: 'ID3',
      name: 'ID3',
      description: 'Iterative Dichotomiser 3 - Algorithme classique basé sur l\'entropie'
    },
    {
      id: 'CART',
      name: 'CART',
      description: 'Classification and Regression Trees - Utilise l\'indice de Gini'
    },
    {
      id: 'RandomForest',
      name: 'Random Forest',
      description: 'Ensemble d\'arbres de décision pour une meilleure précision'
    }
  ];

  const handleTrain = async () => {
    setTraining(true);
    setError(null);
    setProgress(0);

    // Simulation du progrès
    const progressInterval = setInterval(() => {
      setProgress(prev => Math.min(prev + 10, 90));
    }, 200);

    const trainingRequest = {
      algorithm: algorithm,
      numberOfTrees: algorithm === 'RandomForest' ? numberOfTrees : 100
    };

    try {
      const response = await fetch('http://localhost:8080/api/model/train', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(trainingRequest),
      });

      const result = await response.json();

      clearInterval(progressInterval);
      setProgress(100);

      if (result.success) {
        setTimeout(() => {
          onModelTrained(result);
        }, 500);
      } else {
        setError(result.message);
      }
    } catch (err) {
      clearInterval(progressInterval);
      setError('Erreur de connexion au serveur. Assurez-vous que le backend est démarré.');
    } finally {
      setTimeout(() => {
        setTraining(false);
        setProgress(0);
      }, 1000);
    }
  };

  if (!uploadedData) {
    return (
      <Alert>
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>
          Veuillez d'abord charger un fichier de données dans l'onglet précédent.
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Settings className="h-5 w-5" />
            <span>Configuration du Modèle</span>
          </CardTitle>
          <CardDescription>
            Sélectionnez l'algorithme et configurez les paramètres d'entraînement
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div>
            <Label className="text-base font-medium mb-4 block">
              Algorithme d'Apprentissage
            </Label>
            <RadioGroup value={algorithm} onValueChange={setAlgorithm}>
              {algorithms.map((algo) => (
                <div key={algo.id} className="flex items-start space-x-3 p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800">
                  <RadioGroupItem value={algo.id} id={algo.id} className="mt-1" />
                  <div className="flex-1">
                    <Label htmlFor={algo.id} className="font-medium cursor-pointer">
                      {algo.name}
                    </Label>
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                      {algo.description}
                    </p>
                  </div>
                </div>
              ))}
            </RadioGroup>
          </div>

          {algorithm === 'RandomForest' && (
            <div>
              <Label htmlFor="numberOfTrees" className="text-base font-medium">
                Nombre d'Arbres
              </Label>
              <Input
                id="numberOfTrees"
                type="number"
                value={numberOfTrees}
                onChange={(e) => setNumberOfTrees(parseInt(e.target.value) || 100)}
                min="1"
                max="1000"
                className="mt-2"
              />
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                Plus d'arbres améliore généralement la précision mais augmente le temps de calcul
              </p>
            </div>
          )}

          <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg">
            <h4 className="font-medium mb-2">Résumé des Données</h4>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500">Instances:</span>
                <span className="ml-2 font-medium">{uploadedData.instanceCount}</span>
              </div>
              <div>
                <span className="text-gray-500">Attributs:</span>
                <span className="ml-2 font-medium">{uploadedData.attributeNames?.length}</span>
              </div>
              <div>
                <span className="text-gray-500">Classe cible:</span>
                <span className="ml-2 font-medium">{uploadedData.className}</span>
              </div>
              <div>
                <span className="text-gray-500">Algorithme:</span>
                <span className="ml-2 font-medium">{algorithm}</span>
              </div>
            </div>
          </div>

          {training && (
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Entraînement en cours...</span>
                <span className="text-sm text-gray-500">{progress}%</span>
              </div>
              <Progress value={progress} className="w-full" />
            </div>
          )}

          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <Button
            onClick={handleTrain}
            disabled={training}
            className="w-full"
            size="lg"
          >
            {training ? (
              <>
                <Brain className="h-4 w-4 mr-2 animate-spin" />
                Entraînement en cours...
              </>
            ) : (
              <>
                <Play className="h-4 w-4 mr-2" />
                Démarrer l'Entraînement
              </>
            )}
          </Button>
        </CardContent>
      </Card>
    </div>
  );
};

export default ModelTraining;

