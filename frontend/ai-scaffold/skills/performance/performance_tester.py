from typing import Dict, Any, Callable
import time


class PerformanceTester:
    def measure_response_time(self, func: Callable, iterations: int = 100) -> Dict[str, Any]:
        times = []

        for _ in range(iterations):
            start = time.time()
            func()
            end = time.time()
            times.append(end - start)

        return {
            "average_time": sum(times) / len(times),
            "min_time": min(times),
            "max_time": max(times),
            "iterations": iterations
        }
