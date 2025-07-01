import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Alert, AlertDescription } from './ui/alert';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { BarChart3, Clock, Target, FileText, Download, CheckCircle, AlertCircle } from 'lucide-react';

const Results = ({ trainingResults }) => {
  const [activeTab, setActiveTab] = useState('overview');

  if (!trainingResults) {
    return (
      <Alert>
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>
          Aucun résultat d'entraînement disponible. Veuillez d'abord entraîner un modèle.
        </AlertDescription>
      </Alert>
    );
  }

  const formatTime = (ms) => {
    if (ms < 1000) return `${ms}ms`;
    return `${(ms / 1000).toFixed(2)}s`;
  };

  const formatPercentage = (value) => {
    return `${(value * 100).toFixed(2)}%`;
  };

  // Données pour le graphique de performance par classe
  const classPerformanceData = trainingResults.classMetrics?.map(metric => ({
    name: metric.className,
    precision: metric.precision * 100,
    recall: metric.recall * 100,
    fMeasure: metric.fMeasure * 100
  })) || [];

  // Couleurs pour le graphique
  const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

  const downloadResults = () => {
    const element = document.createElement('a');
    const file = new Blob([trainingResults.detailedOutput], { type: 'text/plain' });
    element.href = URL.createObjectURL(file);
    element.download = `results_${trainingResults.algorithm}_${new Date().toISOString().split('T')[0]}.txt`;
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  return (
    <div className="space-y-6">
      <Alert>
        <CheckCircle className="h-4 w-4" />
        <AlertDescription>
          Modèle {trainingResults.algorithm} entraîné avec succès ! 
          Précision: {formatPercentage(trainingResults.accuracy)}
        </AlertDescription>
      </Alert>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="overview">Vue d'ensemble</TabsTrigger>
          <TabsTrigger value="metrics">Métriques</TabsTrigger>
          <TabsTrigger value="confusion">Matrice</TabsTrigger>
          <TabsTrigger value="details">Détails</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Précision</CardTitle>
                <Target className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-green-600">
                  {formatPercentage(trainingResults.accuracy)}
                </div>
                <p className="text-xs text-muted-foreground">
                  Taux d'erreur: {formatPercentage(trainingResults.errorRate)}
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Temps d'entraînement</CardTitle>
                <Clock className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {formatTime(trainingResults.trainingTimeMs)}
                </div>
                <p className="text-xs text-muted-foreground">
                  Test: {formatTime(trainingResults.testingTimeMs)}
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Algorithme</CardTitle>
                <BarChart3 className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {trainingResults.algorithm}
                </div>
                <p className="text-xs text-muted-foreground">
                  {trainingResults.classMetrics?.length} classes
                </p>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Performance par Classe</CardTitle>
              <CardDescription>
                Comparaison des métriques de précision, rappel et F-mesure
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={classPerformanceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis domain={[0, 100]} />
                  <Tooltip formatter={(value) => `${value.toFixed(2)}%`} />
                  <Bar dataKey="precision" name="Précision" fill="#3b82f6" />
                  <Bar dataKey="recall" name="Rappel" fill="#10b981" />
                  <Bar dataKey="fMeasure" name="F-mesure" fill="#f59e0b" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="metrics" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Métriques Détaillées par Classe</CardTitle>
              <CardDescription>
                Analyse complète des performances pour chaque classe
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Classe</TableHead>
                    <TableHead>Précision</TableHead>
                    <TableHead>Rappel</TableHead>
                    <TableHead>F-mesure</TableHead>
                    <TableHead>Taux VP</TableHead>
                    <TableHead>Taux FP</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {trainingResults.classMetrics?.map((metric, index) => (
                    <TableRow key={index}>
                      <TableCell className="font-medium">
                        <Badge variant="outline">{metric.className}</Badge>
                      </TableCell>
                      <TableCell>{formatPercentage(metric.precision)}</TableCell>
                      <TableCell>{formatPercentage(metric.recall)}</TableCell>
                      <TableCell>{formatPercentage(metric.fMeasure)}</TableCell>
                      <TableCell>{formatPercentage(metric.tpRate)}</TableCell>
                      <TableCell>{formatPercentage(metric.fpRate)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="confusion" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Matrice de Confusion</CardTitle>
              <CardDescription>
                Analyse des prédictions correctes et incorrectes
              </CardDescription>
            </CardHeader>
            <CardContent>
              {trainingResults.confusionMatrix && (
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead className="w-24">Réel \ Prédit</TableHead>
                        {trainingResults.classMetrics?.map((metric, index) => (
                          <TableHead key={index} className="text-center">
                            {metric.className}
                          </TableHead>
                        ))}
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {trainingResults.confusionMatrix.map((row, rowIndex) => (
                        <TableRow key={rowIndex}>
                          <TableCell className="font-medium">
                            {trainingResults.classMetrics?.[rowIndex]?.className}
                          </TableCell>
                          {row.map((cell, cellIndex) => (
                            <TableCell 
                              key={cellIndex} 
                              className={`text-center ${
                                rowIndex === cellIndex 
                                  ? 'bg-green-50 dark:bg-green-900/20 font-bold' 
                                  : cell > 0 
                                    ? 'bg-red-50 dark:bg-red-900/20' 
                                    : ''
                              }`}
                            >
                              {cell}
                            </TableCell>
                          ))}
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="details" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <span className="flex items-center space-x-2">
                  <FileText className="h-5 w-5" />
                  <span>Rapport Détaillé</span>
                </span>
                <Button onClick={downloadResults} variant="outline" size="sm">
                  <Download className="h-4 w-4 mr-2" />
                  Télécharger
                </Button>
              </CardTitle>
              <CardDescription>
                Sortie complète de l'évaluation du modèle
              </CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="text-xs bg-gray-50 dark:bg-gray-900 p-4 rounded-lg overflow-x-auto whitespace-pre-wrap font-mono">
                {trainingResults.detailedOutput}
              </pre>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default Results;

