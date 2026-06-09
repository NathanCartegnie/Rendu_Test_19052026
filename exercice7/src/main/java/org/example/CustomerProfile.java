package org.example;

public enum CustomerProfile {
	STANDARD(0.0),
	PREMIUM(0.10),
	VIP(0.20);

	private final double discountRate;

	CustomerProfile(double discountRate) {
		this.discountRate = discountRate;
	}

	public double getDiscountRate() {
		return discountRate;
	}
}


