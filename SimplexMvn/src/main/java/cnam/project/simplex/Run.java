package cnam.project.simplex;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.pipe.SpanShapeRenderer.Simple;

public class Run
{
    
    final private static Logger LOG = (Logger) LoggerFactory.getLogger(Run.class);
    
    
    
    public static void main(String[] args)
    {
        boolean quit = false;
    
        /*
         * Exemple de probleme :
         * Maximiser 3x + 5y
         * x + y = 4
         * et
         * x + 3y = 6
         */
        
        float[][] standardized = {
            {1, 1, 1, 0, 4},
            {1, 3, 0, 1, 6},
            {-3, -5, 0, 0, 0}
        };
        
        // Ligne & colonne n'incluent pas les "right hand values" et "objective rows"
        Simplex simplex = new Simplex(2, 4);
        
        // Remplir le tableau
        simplex.fillArray(standardized);
        
        // Afficher le tableau
        LOG.info("-----Tableau de depart-----");
        simplex.print();
        
        // Si le tableau n'est pas optimal, on réitère
        while (!quit)
        {
            SOLUTION sol = simplex.process();
            
            if (sol == SOLUTION.IS_OPTIMAL)
            {
                simplex.print();
                quit = true;
            }
            else if (sol == SOLUTION.UNBOUNDED)
            {
                LOG.info("-----La solution est indéfinie-----");
                quit = true;
            }
        }
    }
}
