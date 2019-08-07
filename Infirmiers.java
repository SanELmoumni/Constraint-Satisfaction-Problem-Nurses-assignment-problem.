import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
public class Infirmiers {
public static void main(String[] args) {
        // The model is the main component of Choco Solver
        CPModel Mymodel = new CPModel();
        int N = 7;  //nombre des infirmiers
        int P = 6;  //nombre des postes
        int NJours = 7; //nombre de jours
        int[][] C = new int[N][NJours];   // matrice represente les jours des congés des infirmiers
        
                            // Declaration des variables
       
        IntegerVariable[][][] X = new IntegerVariable[N][P][NJours];       //matrice des affectations (inconnue)
        IntegerVariable[] Temp1 = new IntegerVariable[N];         //ces variables sont temporaires pour stocker la somme des variables
        IntegerVariable[] Temp2 = new IntegerVariable[N];
        IntegerVariable[] Temp3 = new IntegerVariable[N];
        IntegerVariable[] Temp4 = new IntegerVariable[P];
        IntegerVariable[] Temp5 = new IntegerVariable[3*N];
        IntegerVariable[] TempC = new IntegerVariable[P];
        
                            // l'ajout de ces variables au model
        
        for(int i=0;i<N;i++)
            for(int j=0;j<P;j++)
                for(int t=0;t<NJours;t++) {
                    X[i][j][t]=Choco.makeIntVar("X"+i+j+t, 0, 1);
                    Mymodel.addVariable(X[i][j][t]);}
                            
                            // Declaration des contraintes

            for(int t=0;t<NJours;t++) {
                  // Contraite que la somme doit etre egale à 1 pour le poste 1  
        for(int i=0;i<N;i++) {
            Temp1[i]=X[i][0][t];
        }
        Mymodel.addConstraint(Choco.eq(1, Choco.sum(Temp1)));
                  // Contraite que la somme doit etre egale à 1 pour le poste 6
        for(int i=0;i<N;i++) {
            Temp2[i]=X[i][5][t];
        }
        Mymodel.addConstraint(Choco.eq(1, Choco.sum(Temp2)));
                    // Contrainte que 2 infirmiers doivent etre au poste 5
        for(int i=0;i<N;i++) {
            Temp3[i]=X[i][4][t];
        }
        Mymodel.addConstraint(Choco.eq(2, Choco.sum(Temp3)));
                  //un infirmier ne peut pas occuper deux postes a la fois, au plus un seul
        for(int i=0;i<N;i++) {
          for(int j=0;j<P;j++) {
            Temp4[j]=X[i][j][t];
        }

        Mymodel.addConstraint(Choco.gt(2, Choco.sum(Temp4)));     // Temps4<2 
        }
                  //contrainte : les postes 2, 3 et 4 doivent contenir au total 2 ou 3 infirmiers  
        for (int i=0;i<N;i++) {
          Temp5[i]=X[i][1][t];
        }
        for (int i=0;i<N;i++) {
          Temp5[i+N]=X[i][2][t];
        }
        for (int i=0;i<N;i++) {
          Temp5[i+2*N]=X[i][3][t];
        }
        Mymodel.addConstraint(Choco.gt(Choco.sum(Temp5),1));
        Mymodel.addConstraint(Choco.gt(4, Choco.sum(Temp5)));
          }
            //Initialisation de la matrice C 
                for(int i=0;i<N;i++) {
                  for(int j=0;j<NJours;j++) {
                    C[i][j]=0;
                  }
                }
                // Les infirmiers 3,1 et 5 sont en congé respectivement le jour 1,5 et 7
                C[2][0]=1;
                C[0][4]=1;
                C[4][6]=1;
                
                      //Contrainte: Un infirmier qui n'est pas en congé doit travailler
                
                for(int t=0;t<NJours;t++) {
                  for(int i=0;i<N;i++) {
                    for(int j=0;j<P;j++) {
                      TempC[j] = X[i][j][t];
                    }
                    Mymodel.addConstraint(Choco.eq(1-C[i][t], Choco.sum(TempC)));
                  }
                }
                
        CPSolver s = new CPSolver();
        s.read(Mymodel);
        s.solve();
        if(s.isFeasible()){
          for (int t=0;t<NJours;t++) {
            System.out.println("Le jour : "+(t+1)); 
          
          System.out.println("    	Le Poste j ");
          System.out.println("		1	2	3	4	5	6");
          System.out.println("l'infirmier i ");
            System.out.println("\t");
              for (int i=0;i<N;i++) {
                System.out.print("  	"+(i+1));
                for (int j=0;j<P;j++)
                  System.out.print("     	" +s.getVar(X[i][j][t]).getVal());
                System.out.println("");
                          }
          }
        }
    }
}
