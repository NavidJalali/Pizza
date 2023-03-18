package de.riskident.models

enum ParsingError:
  self =>
  case UnexpectedEndOfStream
  case InvalidCustomerCount(invalidInput: String)
  case InvalidOrderEntry(invalidInput: String)

  override def toString: String = self match
    case UnexpectedEndOfStream              => "Unexpected end of stream"
    case InvalidCustomerCount(invalidInput) => s"Invalid customer count: $invalidInput"
    case InvalidOrderEntry(invalidInput)    => s"Invalid order entry: $invalidInput"
