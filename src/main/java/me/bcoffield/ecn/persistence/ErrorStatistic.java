package me.bcoffield.ecn.persistence;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorStatistic {
  private List<Long> occurrences = new ArrayList<>();
}
