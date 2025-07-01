import React, { useState, useRef } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Alert, AlertDescription } from './ui/alert';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Upload, FileText, CheckCircle, AlertCircle } from 'lucide-react';

const DataUpload = ({ onDataUploaded }) => {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState(null);
  const [error, setError] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileSelect = (event) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) {
      if (!selectedFile.name.toLowerCase().endsWith('.arff')) {
        setError('Seuls les fichiers ARFF sont supportés');
        return;
      }
      setFile(selectedFile);
      setError(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Veuillez sélectionner un fichier');
      return;
    }

    setUploading(true);
    setError(null);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:8080/api/data/upload', {
        method: 'POST',
        body: formData,
      });

      const result = await response.json();

      if (result.success) {
        setUploadResult(result);
        onDataUploaded(result);
      } else {
        setError(result.message);
      }
    } catch (err) {
      setError('Erreur de connexion au serveur. Assurez-vous que le backend est démarré.');
    } finally {
      setUploading(false);
    }
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = (event) => {
    event.preventDefault();
    const droppedFile = event.dataTransfer.files[0];
    if (droppedFile) {
      if (!droppedFile.name.toLowerCase().endsWith('.arff')) {
        setError('Seuls les fichiers ARFF sont supportés');
        return;
      }
      setFile(droppedFile);
      setError(null);
    }
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Upload className="h-5 w-5" />
            <span>Chargement des Données</span>
          </CardTitle>
          <CardDescription>
            Chargez un fichier ARFF contenant vos données d'entraînement
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div
            className="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center hover:border-gray-400 dark:hover:border-gray-500 transition-colors cursor-pointer"
            onDragOver={handleDragOver}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
          >
            <FileText className="h-12 w-12 mx-auto text-gray-400 mb-4" />
            <p className="text-lg font-medium text-gray-900 dark:text-white mb-2">
              {file ? file.name : 'Glissez-déposez votre fichier ARFF ici'}
            </p>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              ou cliquez pour sélectionner un fichier
            </p>
            <input
              ref={fileInputRef}
              type="file"
              accept=".arff"
              onChange={handleFileSelect}
              className="hidden"
            />
          </div>

          {file && (
            <div className="mt-4 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <FileText className="h-4 w-4 text-blue-500" />
                  <span className="text-sm font-medium">{file.name}</span>
                  <span className="text-xs text-gray-500">
                    ({(file.size / 1024).toFixed(1)} KB)
                  </span>
                </div>
                <Button
                  onClick={handleUpload}
                  disabled={uploading}
                  className="ml-4"
                >
                  {uploading ? 'Chargement...' : 'Charger'}
                </Button>
              </div>
            </div>
          )}

          {error && (
            <Alert className="mt-4" variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {uploadResult && (
            <Alert className="mt-4">
              <CheckCircle className="h-4 w-4" />
              <AlertDescription>
                Fichier chargé avec succès ! {uploadResult.instanceCount} instances trouvées.
              </AlertDescription>
            </Alert>
          )}
        </CardContent>
      </Card>

      {uploadResult && (
        <Card>
          <CardHeader>
            <CardTitle>Aperçu des Données</CardTitle>
            <CardDescription>
              Classe cible: {uploadResult.className} | 
              Attributs: {uploadResult.attributeNames?.length} | 
              Instances: {uploadResult.instanceCount}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    {uploadResult.attributeNames?.map((attr, index) => (
                      <TableHead key={index}>{attr}</TableHead>
                    ))}
                    <TableHead className="font-semibold text-blue-600">
                      {uploadResult.className}
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {uploadResult.preview?.map((instance, index) => (
                    <TableRow key={index}>
                      <TableCell className="font-medium">{instance.id}</TableCell>
                      {instance.values?.map((value, valueIndex) => (
                        <TableCell key={valueIndex}>{value}</TableCell>
                      ))}
                      <TableCell className="font-semibold text-blue-600">
                        {instance.classLabel}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
            {uploadResult.instanceCount > 10 && (
              <p className="text-sm text-gray-500 mt-2">
                Affichage des 10 premières instances sur {uploadResult.instanceCount} au total.
              </p>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default DataUpload;

