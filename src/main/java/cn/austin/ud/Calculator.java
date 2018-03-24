package cn.austin.ud;

import java.util.Collections;
import java.util.Stack;

public class Calculator {

    private Stack postfixStack = new Stack();
    private Stack opStack = new Stack();
    private int[] operatPriority = new int[]{0, 3, 2, 1, -1, 1, 0, 2};

    public double calculate(String expression) {
        Stack resultStack = new Stack();
        this.prepare(expression);
        Collections.reverse(this.postfixStack);

        while (!this.postfixStack.isEmpty()) {
            String currentValue = (String) this.postfixStack.pop();
            if (!this.isOperator(currentValue.charAt(0))) {
                resultStack.push(currentValue);
            } else {
                String secondValue = (String) resultStack.pop();
                String firstValue = (String) resultStack.pop();
                String tempResult = this.calculate(firstValue, secondValue, currentValue.charAt(0));
                resultStack.push(tempResult);
            }
        }

        return Double.valueOf((String) resultStack.pop());
    }

    private void prepare(String expression) {
        this.opStack.push(',');
        char[] arr = expression.toCharArray();
        int currentIndex = 0;
        int count = 0;

        for (int i = 0; i < arr.length; ++i) {
            char currentOp = arr[i];
            if (this.isOperator(currentOp)) {
                if (count > 0) {
                    this.postfixStack.push(new String(arr, currentIndex, count));
                }

                char peekOp = (Character) this.opStack.peek();
                if (currentOp == 41) {
                    while ((Character) this.opStack.peek() != 40) {
                        this.postfixStack.push(String.valueOf(this.opStack.pop()));
                    }

                    this.opStack.pop();
                } else {
                    while (currentOp != 40 && peekOp != 44 && this.compare(currentOp, peekOp)) {
                        this.postfixStack.push(String.valueOf(this.opStack.pop()));
                        peekOp = (Character) this.opStack.peek();
                    }

                    this.opStack.push(currentOp);
                }

                count = 0;
                currentIndex = i + 1;
            } else {
                ++count;
            }
        }

        if (count > 1 || count == 1 && !this.isOperator(arr[currentIndex])) {
            this.postfixStack.push(new String(arr, currentIndex, count));
        }

        while ((Character) this.opStack.peek() != 44) {
            this.postfixStack.push(String.valueOf(this.opStack.pop()));
        }

    }

    private boolean isOperator(char c) {
        return c == 43 || c == 45 || c == 42 || c == 47 || c == 40 || c == 41;
    }

    public boolean compare(char cur, char peek) {
        boolean result = false;
        if (this.operatPriority[peek - 40] >= this.operatPriority[cur - 40]) {
            result = true;
        }

        return result;
    }

    private String calculate(String firstValue, String secondValue, char currentOp) {
        double result = 0.0D;
        double f = Double.parseDouble(firstValue);
        double s = Double.parseDouble(secondValue);
        switch (currentOp) {
            case 42:
                result = f * s;
                break;
            case 43:
                result = f + s;
            case 44:
            case 46:
            default:
                break;
            case 45:
                result = f - s;
                break;
            case 47:
                if (s == 0.0D) {
                    result = 0.0D;
                } else {
                    result = f / s;
                }
        }

        return String.valueOf(result);
    }
}
