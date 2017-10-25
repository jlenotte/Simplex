package cnam.project.simplex;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Algorithme de programmation linéaire "Simplex"
 * ==================================================================================
 * Nous créons une instance du tableau Simplex
 * Nous remplissons un tableau avec la méthode Simplex.fillArray()
 * Nous créons une boucle qui appelle Simplex.process() jusqu'à ce que la solution
 * SOLUTION.IS_OPTIMAL ou SOLUTION.UNBOUNDED soit retournée.
 * ==================================================================================
 */
public class Simplex
{
    
    // Logger
    final private static Logger LOG = (Logger) LoggerFactory.getLogger(Simplex.class);
    
    // Rows & Columns
    private int _row;
    private int _col;
    
    // Simplex Array
    public static float[][] array;
    
    // Solution qui peut être augmentée à l'infini (Y/N)
    private boolean solutionIsUnbounded = false;
    
    
    
    /** L'objet Simplex prends en paramètres le nb de contraintes et le nb de variables */
    public Simplex(int _contraintes, int _inconnues)
    {
        _row = _contraintes + 1;
        _col = _inconnues + 1;
        
        // Creation d'un 2ème Array
        array = new float[_row][];
        
        // Initialiser les références de l'Array
        for (int i = 0; i < _row; i++)
        {
            array[i] = new float[_col];
        }
    }
    
    
    
    /** Afficher le tableau Simplex */
    public void print()
    {
        for (int i = 0; i < _row; i++)
        {
            for (int j = 0; j < _col; j++)
            {
                String value = String.format("%.2f", array[i][j]);
                LOG.info("VALUES : {} \t", value);
            }
        }
    }
    
    
    
    /** Remplir le tableau avec des coefficients */
    public void fillArray(float[][] data)
    {
        for (int i = 0; i < array.length; i++)
        {
            System.arraycopy(data[i], 0, this.array[i], 0, data[i].length);
        }
    }
    
    
    
    /**
     * Calcul des valeurs du tableau simplex
     * Utilise une boucle pour continuellement calculer jusqu'à ce que la solution optimale soit
     * trouvée
     */
    public SOLUTION process()
    {
        // Etape 1
        if (checkOptimal())
        {
            return SOLUTION.IS_OPTIMAL;
        }
        
        // Etape 2 - Trouver la colonne IN
        int _pivotCol = findEnteringCol();
        LOG.info("Pivot colonne : {}", _pivotCol);
        
        // Etape 3 - Trouver la valeur de départ
        float[] _ratios = calculateRatios(_pivotCol);
        
        if (solutionIsUnbounded == true)
        {
            return SOLUTION.UNBOUNDED;
        }
        
        int _pivotRow = findSmallestValue(_ratios);
        
        // Etape 4 - Créer le nouveau tableau
        createNextArray(_pivotRow, _pivotCol);
        
        // Vu que bous avons formé un nouveau tableau, la solution n'est pas optimale
        return SOLUTION.NOT_OPTIMAL;
    }
    
    
    
    /** Calcule les ratios */
    private float[] calculateRatios(int _pivotCol)
    {
        float[] positiveValues = new float[_row];
        float[] res = new float[_row];
        int _allNegCount = 0;
        
        for (int i = 0; i < _row; i++)
        {
            if (array[i][_col] > 0)
            {
                positiveValues[i] = array[i][_col];
            }
            else
            {
                positiveValues[i] = 0;
                _allNegCount++;
            }
        }
        
        if (_allNegCount == _row)
        {
            this.solutionIsUnbounded = true;
        }
        else
        {
            for (int i = 0; i < _row; i++)
            {
                float _value = positiveValues[i];
                
                if (_value > 0)
                {
                    res[i] = array[i][_col - 1] / _value;
                }
            }
        }
        
        return res;
    }
    
    
    
    /** Crée un nouveau tableau à partir des valeurs précalculées */
    private void createNextArray(int _pivotRow, int _pivotCol)
    {
        float _pivotValue = array[_pivotRow][_pivotCol];
        float[] pivotRowValues = new float[_col];
        float[] pivotColValues = new float[_col];
        float[] newRows = new float[_col];
        
        // Diviser les entrées de la ligne pivot par la colonne pivot
        System.arraycopy(array[_pivotRow], 0, pivotRowValues, 0, _col);
        
        // Obtenir l'entrée de la colonne du pivot
        for (int i = 0; i < _row; i++)
        {
            pivotColValues[i] = array[i][_pivotCol];
        }
        
        // Diviser les valeurs de la ligne pivot par la valeur
        for (int i = 0; i < _col; i++)
        {
            newRows[i] = pivotRowValues[i] / _pivotValue;
        }
        
        // Soustraire par chacun des autres lignes
        for (int i = 0; i < _row; i++)
        {
            if (i != _pivotRow)
            {
                for (int j = 0; j < _col; j++)
                {
                    float _c = pivotColValues[i];
                    array[i][j] = array[i][j] - (_c * newRows[j]);
                }
            }
        }
        
        // Remplacer la ligne
        System.arraycopy(newRows, 0, array[_pivotRow], 0, newRows.length);
    }
    
    
    /** Trouve le point d'entrée de colonne */
    private int findEnteringCol()
    {
        float[] values = new float[_col];
        int _location = 0;
        int _position = 0;
        int _count = 0;
        
        for (_position = 0; _position < _col - 1; _position++)
        {
            if (array[_row - 1][_position] < 0)
            {
                _count++;
            }
        }
        
        if (_count > 1)
        {
            for (int i = 0; i < _col - 1; i++)
            {
                values[i] = Math.abs(array[_row - 1][i]);
            }
            _location = findLargestValue(values);
        }
        else
        {
            _location = _count - 1;
        }
        
        return _location;
    }
    
    
    
    /** Trouve la plus petite valeur */
    private int findSmallestValue(float[] values)
    {
        float _minimum;
        int c = 0;
        int _location = 0;
        _minimum = values[0];
        
        for (c = 1; c < values.length; c++)
        {
            if (values[c] > 0)
            {
                if (Float.compare(values[c], _minimum) < 0)
                {
                    _minimum = values[c];
                    _location = c;
                }
            }
        }
        
        return _location;
    }
    
    
    
    /** Trouve la plus grande valeur */
    private int findLargestValue(float[] values)
    {
        float _max = 0;
        int c = 0;
        int _location = 0;
        
        _max = values[0];
        
        for (c = 1; c < values.length; c++)
        {
            if (Float.compare(values[c], _max) > 0)
            {
                _max = values[c];
                _location = c;
            }
        }
        
        return _location;
    }
    
    
    
    /** Vérifie que la table est optimale */
    private boolean checkOptimal()
    {
        boolean isOptimal = false;
        int _vCount = 0;
        
        for (int i = 0; i < _col - 1; i++)
        {
            float _val = array[_row - 1][i];
            
            if (_val >= 0)
            {
                _vCount++;
            }
        }
        
        if (_vCount == _col - 1)
        {
            isOptimal = true;
        }
        
        return isOptimal;
    }
    
    
    /** Return le tableau Simplex */
    public float[][] getArray()
    {
        return array;
    }
}
