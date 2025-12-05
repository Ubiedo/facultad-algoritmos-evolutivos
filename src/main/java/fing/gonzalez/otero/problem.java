package fing.gonzalez.otero;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import java.util.List;

public class problem implements DoubleProblem {

    private int numberOfVariables;
    private int numberOfObjectives;

    public problem(int numberOfNodos) {
        this.numberOfVariables = numberOfNodos;
        this.numberOfObjectives = 2; // Coste operativo, tiempo medio de entrega
    }

    @Override
    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    @Override
    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return "Distribution route with N vehicles";
    }

	@Override
	public Double getLowerBound(int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getLowerBound'");
	}

	@Override
	public Double getUpperBound(int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getUpperBound'");
	}

	@Override
	public List<Pair<Double, Double>> getBounds() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getBounds'");
	}

	@Override
	public void evaluate(DoubleSolution solution) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
	}

	@Override
	public DoubleSolution createSolution() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createSolution'");
	}
}
