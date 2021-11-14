//package com.example
//
//package object model {
//
//  sealed trait LogicOperation
//  case object AND extends LogicOperation
//  case object OR extends LogicOperation
//
//  sealed trait Value[T] {
//    def compare[T: Ordering](other: T): Boolean
//  }
//  sealed trait ValueEnrichmentOperation[T] {
//    def enrich(): PlainValue[T]
//  }
//  case class Multiply[T](right: Value[T], left: Value[T]) extends ValueEnrichmentOperation[T] {
//    override def enrich(): PlainValue[T] = {
//      val r = validate(getPlainValue(right))
//      val l = validate(getPlainValue(left))
//      calculate(r, l)
//    }
//
//    private def getPlainValue(value: Value[T]): PlainValue[T] = {
//      value match {
//        case plainValue: PlainValue[T] => plainValue
//        case EnrichedValue(valueEnrichmentOperation) => valueEnrichmentOperation.enrich()
//      }
//    }
//
//    private def validate(plainValue: PlainValue[T]): PlainValue[T] = {
//      plainValue match {
//        case _: PlainValue[String] => throw new UnsupportedOperationException("Multiplying of strings is not supported")
//        case _ => plainValue
//      }
//    }
//
//    private def calculate(left: PlainValue[T], right: PlainValue[T]): PlainValue[T] = {
//      (left, right) match {
//        case (l: IntegerValue, r: IntegerValue) => IntegerValue(l.value * r.value)
//        case (l: NumberValue, r: NumberValue) => NumberValue(l.value * r.value)
//        case (l: NumberValue, r: IntegerValue) => NumberValue(l.value * r.value)
//        case (l: IntegerValue, r: NumberValue) => NumberValue(l.value * r.value)
//        case _ => throw new UnsupportedOperationException("Adding other types than IntegerValue or NumberValue is not supported")
//      }
//    }
//  }
//  case class Add[T](right: Value[T], left: Value[T]) extends ValueEnrichmentOperation[T] {
//    override def enrich(): PlainValue = {
//      val r = validate(getPlainValue(right))
//      val l = validate(getPlainValue(left))
//      calculate(r, l)
//    }
//
//    private def getPlainValue(value: Value): PlainValue = {
//      value match {
//        case plainValue: PlainValue => plainValue
//        case EnrichedValue(valueEnrichmentOperation) => valueEnrichmentOperation.enrich()
//      }
//    }
//
//    private def validate(plainValue: PlainValue): PlainValue = {
//      plainValue match {
//        case _: StringValue => throw new UnsupportedOperationException("Multiplying of strings is not supported")
//        case _ => plainValue
//      }
//    }
//
//    private def calculate(left: PlainValue, right: PlainValue): PlainValue = {
//      (left, right) match {
//        case (l: IntegerValue, r: IntegerValue) => IntegerValue(l.value + r.value)
//        case (l: NumberValue, r: NumberValue) => NumberValue(l.value + r.value)
//        case (l: NumberValue, r: IntegerValue) => NumberValue(l.value + r.value)
//        case (l: IntegerValue, r: NumberValue) => NumberValue(l.value + r.value)
//        case _ => throw new UnsupportedOperationException("Adding other types than IntegerValue or NumberValue is not supported")
//      }
//    }
//  }
//
//  case class PlainValue[T: Ordering](value: T) extends Value[T] {
//    override def compare[T: Ordering](other: T): Boolean = Ordering[T].gt(value, other)
//  }
//  case class EnrichedValue[T](value: ValueEnrichmentOperation[T]) extends Value[T] {
//    override def compare[T: Ordering](other: T): Boolean = Ordering[T].gt(value, other)
//  }
//
//  sealed trait Evaluation {
//    def evaluate(): Boolean
//  }
//  case class Gt(right: Value, left: Value) extends Evaluation {
//    override def evaluate(): Boolean = right
//  }
//  case class Lt(right: Value, left: Value) extends Evaluation
//  case class Eq(right: Value, left: Value) extends Evaluation
//  case class OneOf(values: List[Value], evaluationCriterion: Evaluation) extends Evaluation
//
//  sealed trait Expression
//
//  case class LogicExpression(operation: LogicOperation,
//                             left: Expression,
//                             right: Expression
//                            ) extends Expression
//  case class EvaluationExpression(
//
//
//                                 ) extends Expression
//
//  case object EmptyExpression extends Expression
//
//}
